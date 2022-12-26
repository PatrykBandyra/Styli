import contextlib
import logging
import os
from io import BytesIO

import pystiche
import torch
import uvicorn
from PIL import Image
from fastapi import FastAPI, UploadFile, File
from fastapi.exceptions import HTTPException
from fastapi.responses import StreamingResponse
from pystiche.image.io import import_from_pil, export_to_pil
from torchvision import transforms

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
formatter = logging.Formatter('%(asctime)s: %(levelname)s: %(name)s: %(message)s')
file_handler = logging.FileHandler('app.log', mode='w')
file_handler.setFormatter(formatter)
logger.addHandler(file_handler)

app = FastAPI()
ROOT_URL = '/st2'

if torch.cuda.is_available():
    device = torch.device('cuda')
else:
    device = torch.device('cpu')


def get_state_dict():
    try:  # local
        return torch.load('model.pth')
    except FileNotFoundError:
        try:  # docker
            return torch.load('app/model.pth')
        except FileNotFoundError:
            @contextlib.contextmanager
            def suppress_output():
                with open(os.devnull, 'w') as devnull:
                    with contextlib.redirect_stdout(devnull), contextlib.redirect_stderr(devnull):
                        yield

            url = "https://download.pystiche.org/models/example_transformer.pth"

            with suppress_output():
                return torch.hub.load_state_dict_from_url(url)


transformer = pystiche.demo.transformer().to(device)
transformer.load_state_dict(get_state_dict())
transformer.eval()


def perform_nst(input_image: Image) -> torch.Tensor:
    image_tensor = transforms.ToTensor()(input_image)
    print(f'Image tensor size: {image_tensor.size()}')
    if len(list(image_tensor.size())) == 4:
        image_tensor = image_tensor.squeeze(0)
    image_tensor.to(device)
    print(f'Image tensor size: {image_tensor.size()}')

    with torch.no_grad():
        output_image_tensor = transformer(image_tensor)
    return output_image_tensor


@app.post(f'{ROOT_URL}/nst')
async def neural_style_transfer(image: UploadFile = File(...)):
    if image.content_type not in ['image/jpeg', 'image/png']:
        raise HTTPException(400, detail='Invalid file type')

    original_image = Image.open(image.file)
    original_image_tensor = import_from_pil(original_image, device=device, make_batched=True)
    nst_image = transformer(original_image_tensor)
    pillow_nst_image = export_to_pil(nst_image)

    response_image = BytesIO()
    pillow_nst_image.save(response_image, 'JPEG')
    response_image.seek(0)

    return StreamingResponse(response_image, media_type='image/jpeg')


@app.get(f'{ROOT_URL}/health')
async def health_check() -> str:
    logger.info('/health endpoint called')
    return '[ST2]: Health check works'


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8003)
