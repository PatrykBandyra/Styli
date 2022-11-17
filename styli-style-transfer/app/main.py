import logging
import os
from io import BytesIO

import cv2
import numpy as np
import uvicorn
from PIL import Image
from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.responses import StreamingResponse

from cartoonizer import Cartoonizer

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
formatter = logging.Formatter('%(asctime)s: %(levelname)s: %(name)s: %(message)s')
file_handler = logging.FileHandler('app.log', mode='w')
file_handler.setFormatter(formatter)
logger.addHandler(file_handler)

app = FastAPI()
ROOT_URL = '/st'


def create_response_image(img: np.ndarray) -> StreamingResponse:
    pillow_img = Image.fromarray(img)
    response_img = BytesIO()
    pillow_img.save(response_img, 'JPEG')
    response_img.seek(0)

    return StreamingResponse(response_img, media_type='image/jpeg')


@app.post(f'{ROOT_URL}/cartoonization')
async def cartoonize(image: UploadFile = File(...)) -> StreamingResponse:
    logger.info('/cartoonize endpoint called')
    if image.content_type not in ['image/jpeg', 'image/png']:
        raise HTTPException(400, detail='Invalid file type')

    cartoonizer = Cartoonizer(os.path.abspath(os.environ.get('WEIGHTS_DIR', 'saved_models')), 'cpu')
    img = cv2.imdecode(np.frombuffer(image.file.read(), np.uint8), 1)
    cartoon_img = cartoonizer.infer(img)
    cartoon_img_rgb = cv2.cvtColor(cartoon_img, cv2.COLOR_BGR2RGB)

    return create_response_image(cartoon_img_rgb)


@app.get(f'{ROOT_URL}/health')
async def health_check() -> str:
    logger.info('/health endpoint called')
    return '[ST]: Health check works'


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8001)
