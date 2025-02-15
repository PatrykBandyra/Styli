"""
Code copyrights are with: https://github.com/SystemErrorWang/White-box-Cartoonization/
"""
import logging
import os

import cv2
import numpy as np
import tensorflow as tf

import guided_filter
import network

logger = logging.getLogger(__name__)
logger.setLevel(logging.INFO)
formatter = logging.Formatter('%(asctime)s: %(levelname)s: %(name)s: %(message)s')
file_handler = logging.FileHandler('cartoonization.log', mode='w')
file_handler.setFormatter(formatter)
logger.addHandler(file_handler)


class Cartoonizer:
    def __init__(self, weights_dir, gpu):
        if not os.path.exists(weights_dir):
            logger.error(f'Cartoonizer initialization failed. Weights Directory not found: {weights_dir}')
            raise FileNotFoundError('Cartoonizer initialization failed. Weights Directory not found')
        self.load_model(weights_dir, gpu)

    @staticmethod
    def resize_crop(image):
        h, w, c = np.shape(image)
        if min(h, w) > 720:
            if h > w:
                h, w = int(720 * h / w), 720
            else:
                h, w = 720, int(720 * w / h)
        image = cv2.resize(image, (w, h),
                           interpolation=cv2.INTER_AREA)
        h, w = (h // 8) * 8, (w // 8) * 8
        image = image[:h, :w, :]
        return image

    def load_model(self, weights_dir, gpu):
        try:
            tf.compat.v1.disable_eager_execution()
        except:
            pass

        tf.compat.v1.reset_default_graph()

        self.input_photo = tf.compat.v1.placeholder(tf.float32, [1, None, None, 3], name='input_image')
        network_out = network.unet_generator(self.input_photo)
        self.final_out = guided_filter.guided_filter(self.input_photo, network_out, r=1, eps=5e-3)

        all_vars = tf.compat.v1.trainable_variables()
        gene_vars = [var for var in all_vars if 'generator' in var.name]
        saver = tf.compat.v1.train.Saver(var_list=gene_vars)

        if gpu:
            gpu_options = tf.compat.v1.GPUOptions(allow_growth=True)
            device_count = {'GPU': 1}
        else:
            gpu_options = None
            device_count = {'GPU': 0}

        config = tf.compat.v1.ConfigProto(gpu_options=gpu_options, device_count=device_count)

        self.sess = tf.compat.v1.Session(config=config)

        self.sess.run(tf.compat.v1.global_variables_initializer())
        saver.restore(self.sess, tf.train.latest_checkpoint(weights_dir))

    def infer(self, image):
        image = self.resize_crop(image)
        batch_image = image.astype(np.float32) / 127.5 - 1
        batch_image = np.expand_dims(batch_image, axis=0)

        output = self.sess.run(self.final_out, feed_dict={self.input_photo: batch_image})

        output = (np.squeeze(output) + 1) * 127.5
        output = np.clip(output, 0, 255).astype(np.uint8)

        return output
