package com.yc.gas.admin.module.business.identify.camera;

/***************************************************************************************************
 * @file Grab_Callback.java
 * @breif Use functions provided in MvCameraControlWrapper.jar to grab images
 * @author    zhanglei72
 * @date 2020/01/12
 *
 * @warning
 * @version V1.0.0  2020/01/12 Create this file
 *            V1.0.1  2020/02/10 add parameter checking
 * @since 2020/02/10
 **************************************************************************************************/

import MvCameraControlWrapper.*;
import com.yc.gas.admin.module.business.identify.entity.FrontendRequest;
import com.yc.gas.admin.module.business.identify.entity.ImageCutParam;
import com.yc.gas.admin.module.business.identify.entity.ModelInput;
import com.yc.gas.admin.module.business.identify.imageprocess.ImageProcessor;
import com.yc.gas.admin.module.business.identify.websocket.GasDataWebSocket;
import com.yc.gas.admin.module.business.identify.websocket.MvsWebSocket;
import com.yc.gas.admin.module.business.identify.camera.LinkPython;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.websocket.Session;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static MvCameraControlWrapper.MvCameraControlDefines.*;

public class Grab_Callback {

    // 选择的哪一个摄像头 0 1 2......
    private int camIndex = -1;

    private boolean toCloseMVS = false;

    // 新增：SDK操作全局锁（确保线程安全）
    private static final Object SDK_LOCK1 = new Object();

    // 新增：SDK操作全局锁（确保线程安全）
    private static final Object SDK_LOCK2 = new Object();

    // 在 Grab_Callback 类中添加静态变量
    private static boolean skipDevicePrinting = false;

    /**
     * 打印设备信息
     *
     * @param stDeviceInfo
     */
    private static void printDeviceInfo(MV_CC_DEVICE_INFO stDeviceInfo) {
        // 如果设置了跳过打印标志，则直接返回
        if (skipDevicePrinting) {
            return;
        }

        if (null == stDeviceInfo) {
            System.out.println("stDeviceInfo is null");
            return;
        }

        if ((stDeviceInfo.transportLayerType == MV_GIGE_DEVICE) || (stDeviceInfo.transportLayerType == MV_GENTL_GIGE_DEVICE)) {
            System.out.println("\tCurrentIp:       " + stDeviceInfo.gigEInfo.currentIp);
            System.out.println("\tModel:           " + stDeviceInfo.gigEInfo.modelName);
            System.out.println("\tUserDefinedName: " + stDeviceInfo.gigEInfo.userDefinedName);
        } else if (stDeviceInfo.transportLayerType == MV_USB_DEVICE) {
            System.out.println("\tUserDefinedName: " + stDeviceInfo.usb3VInfo.userDefinedName);
            System.out.println("\tSerial Number:   " + stDeviceInfo.usb3VInfo.serialNumber);
            System.out.println("\tDevice Number:   " + stDeviceInfo.usb3VInfo.deviceNumber);
        } else if (stDeviceInfo.transportLayerType == MV_GENTL_CAMERALINK_DEVICE) {
            System.out.println("\tUserDefinedName: " + stDeviceInfo.cmlInfo.userDefinedName);
            System.out.println("\tSerial Number:   " + stDeviceInfo.cmlInfo.serialNumber);
            System.out.println("\tDevice Number:   " + stDeviceInfo.cmlInfo.DeviceID);
        } else if (stDeviceInfo.transportLayerType == MV_GENTL_CXP_DEVICE) {
            System.out.println("\tUserDefinedName: " + stDeviceInfo.cxpInfo.userDefinedName);
            System.out.println("\tSerial Number:   " + stDeviceInfo.cxpInfo.serialNumber);
            System.out.println("\tDevice Number:   " + stDeviceInfo.cxpInfo.DeviceID);
        } else if (stDeviceInfo.transportLayerType == MV_GENTL_XOF_DEVICE) {
            System.out.println("\tUserDefinedName: " + stDeviceInfo.xofInfo.userDefinedName);
            System.out.println("\tSerial Number:   " + stDeviceInfo.xofInfo.serialNumber);
            System.out.println("\tDevice Number:   " + stDeviceInfo.xofInfo.DeviceID);
        } else {
            System.err.print("Device is not supported! \n");
        }

        System.out.println("");
    }

    /**
     * 打印帧信息
     *
     * @param stFrameInfo
     */
    private static void printFrameInfo(MV_FRAME_OUT_INFO stFrameInfo) {
        if (null == stFrameInfo) {
            System.err.println("stFrameInfo is null");
            return;
        }

        StringBuilder frameInfo = new StringBuilder("");
        frameInfo.append(("\tFrameNum[" + stFrameInfo.frameNum + "]"));
        frameInfo.append("\tWidth[" + stFrameInfo.width + "]");
        frameInfo.append("\tHeight[" + stFrameInfo.height + "]");
        frameInfo.append(String.format("\tPixelType[%#x]", stFrameInfo.pixelType.getnValue()));

        System.out.println(frameInfo.toString());
    }

    /**
     * 选择摄像头
     *
     * @param stDeviceList
     * @return
     */
    public int chooseCamera(ArrayList<MV_CC_DEVICE_INFO> stDeviceList) {
        if (null == stDeviceList) {
            return -1;
        }

        // Choose a device to operate
        if ((camIndex >= 0 && camIndex < stDeviceList.size()) || -1 == camIndex) {
            if (-1 == camIndex) {
                removeLink(camIndex);
                System.out.println("Input error.exit");
                return camIndex;
            }
        } else {
            System.out.println("Input error: " + camIndex + " Over Range:( 0 - " + (stDeviceList.size() - 1) + " )");
            System.out.println("连接第" + ++camIndex + "失败，该设备不存在！");
            removeLink(camIndex);
            return -1;
        }


        if (0 <= camIndex && stDeviceList.size() > camIndex) {
            if ((MV_GIGE_DEVICE == stDeviceList.get(camIndex).transportLayerType) || (MV_GENTL_GIGE_DEVICE == stDeviceList.get(camIndex).transportLayerType)) {
                System.out.println("Connect to camera[" + camIndex + "]: " + stDeviceList.get(camIndex).gigEInfo.userDefinedName);
            } else if (MV_USB_DEVICE == stDeviceList.get(camIndex).transportLayerType) {
                System.out.println("Connect to camera[" + camIndex + "]: " + stDeviceList.get(camIndex).usb3VInfo.userDefinedName);
            } else if (MV_GENTL_CAMERALINK_DEVICE == stDeviceList.get(camIndex).transportLayerType) {
                System.out.println("Connect to camera[" + camIndex + "]: " + stDeviceList.get(camIndex).cmlInfo.DeviceID);
            } else if (MV_GENTL_CXP_DEVICE == stDeviceList.get(camIndex).transportLayerType) {
                System.out.println("Connect to camera[" + camIndex + "]: " + stDeviceList.get(camIndex).cxpInfo.DeviceID);
            } else if (MV_GENTL_XOF_DEVICE == stDeviceList.get(camIndex).transportLayerType) {
                System.out.println("Connect to camera[" + camIndex + "]: " + stDeviceList.get(camIndex).xofInfo.DeviceID);
            } else {
                System.out.println("Device is not supported.");
            }
        } else {
            System.out.println("Invalid index " + camIndex);
            removeLink(camIndex);
            camIndex = -1;
        }

        return camIndex;
    }

    /**
     * 打开摄像头并获取图片回调函数
     *
     * @param index
     */
    public void openCamera(int index) {
        int nRet = MV_OK;
        // 连接结果
        int camResult = -1;

        Handle hCamera = null;
        ArrayList<MV_CC_DEVICE_INFO> stDeviceList = null;
        synchronized (SDK_LOCK1) {
            // 选择摄像头
            camIndex = index;
            // region 连接摄像头

            // Initialize SDK
            nRet = MvCameraControl.MV_CC_Initialize();
            if (MV_OK != nRet) {
                System.err.printf("Initialize SDK fail! nRet [0x%x]\n\n", nRet);
                endMVS(nRet, hCamera);
            }

            // Enumerate  devices
            try {
                stDeviceList = MvCameraControl.MV_CC_EnumDevices(MV_GIGE_DEVICE | MV_USB_DEVICE | MV_GENTL_GIGE_DEVICE | MV_GENTL_CAMERALINK_DEVICE | MV_GENTL_CXP_DEVICE | MV_GENTL_XOF_DEVICE);
            } catch (CameraControlException e) {
                System.err.println("Enumrate devices failed!" + e.toString());
                e.printStackTrace();
                endMVS(nRet, hCamera);
            }

            // 判断摄像头数量是否大于0
            if (0 >= stDeviceList.size()) {
                System.out.println("No devices found!");
                // 如果没有摄像头，则移除全部
                MvsWebSocket.getSessionMap().clear();
                GasDataWebSocket.getSessionMap().clear();
                MvsWebSocket.getGrabCallbackMap().clear();
                endMVS(nRet, hCamera);
            }

            // 控制设备信息打印，避免重复
            boolean originalSkipFlag = Grab_Callback.skipDevicePrinting;
            Grab_Callback.skipDevicePrinting = true;
            try {
                // 循环打印设备信息
                int i = 0;
                for (MV_CC_DEVICE_INFO stDeviceInfo : stDeviceList) {
                    if (null == stDeviceInfo) {
                        continue;
                    }
                    System.out.println("[camera " + (i++) + "]");
                    printDeviceInfo(stDeviceInfo);
                }
            } finally {
                // 恢复原来的打印控制标志
                Grab_Callback.skipDevicePrinting = originalSkipFlag;
            }

            // 新增：摄像头数量不足时直接提示并退出
            if (index >= stDeviceList.size()) {
                System.out.println("第" + (index + 1) + "台摄像头未连接！");
                removeLink(index);
                endMVS(nRet, hCamera);
                return;
            }

            // choose camera
            camResult = chooseCamera(stDeviceList);
            if (-1 == camResult) // 连接失败
            {
                // 断掉连接
                removeLink(index);
                endMVS(nRet, hCamera);
            }

            // Create device handle
            try {
                hCamera = MvCameraControl.MV_CC_CreateHandle(stDeviceList.get(camResult));
            } catch (CameraControlException e) {
                System.err.println("Create handle failed!" + e.toString());
                e.printStackTrace();
                hCamera = null;
                // 断掉连接
                removeLink(index);
                endMVS(nRet, hCamera);
            }

            // Open selected device
            nRet = MvCameraControl.MV_CC_OpenDevice(hCamera);
            if (MV_OK != nRet) {
                // 断掉连接
                removeLink(index);
                System.err.printf("Connect to camera failed, errcode: [%#x]\n", nRet);
                endMVS(nRet, hCamera);
            }
            // endregion
        }
        /**
         * 回调函数
         */
        do
        /**
         * 创建图像回调方法
         */ {
            // 图像回调方法
            nRet = MvCameraControl.MV_CC_RegisterImageCallBack(hCamera, new CameraImageCallBack() {

                // 在 Grab_Callback 类中添加成员变量，确保不被频繁创建
                private boolean connectionClosedLogged = false;

                @Override
                public int OnImageCallBack(byte[] bytes, MV_FRAME_OUT_INFO mv_frame_out_info) {
                    // 添加调试输出
                    System.out.println("收到图像数据: " + (bytes != null ? bytes.length : 0) + " 字节");
                    System.out.println("图像尺寸: " + mv_frame_out_info.width + "x" + mv_frame_out_info.height);
                    /**
                     * 新增：检查WebSocket连接状态
                     *
                     */
                    Session session = MvsWebSocket.getSessionMap().get(camIndex);
                    if (session == null || !session.isOpen()) {
                        // 只在第一次检测到连接关闭时打印日志
                        if (!connectionClosedLogged) {
                            System.out.println("WebSocket连接已关闭，停止发送帧数据 cameraId=" + camIndex);
                            connectionClosedLogged = true;
                        }
                        return 0; // 直接返回，不执行后续发送操作
                    }

                    // 连接恢复时重置标志
                    connectionClosedLogged = false;


                    int mvsWsCount = MvsWebSocket.activityConnectedSize();
                    if (mvsWsCount > 0) {
                        // 1. 原始BayerRG8数据转RGB（调用修改后的转换方法）
                        byte[] rgbBytes = convertBayerRG8ToRGB(bytes, mv_frame_out_info.width, mv_frame_out_info.height, mv_frame_out_info.width);

                        // 新增：将RGB数据转换为BufferedImage并进行裁切
                        try {
                            // 创建BufferedImage用于保存RGB数据
                            BufferedImage rgbImage = new BufferedImage(
                                    mv_frame_out_info.width,
                                    mv_frame_out_info.height,
                                    BufferedImage.TYPE_3BYTE_BGR
                            );

                            // 将RGB数据写入图像
                            rgbImage.getRaster().setDataElements(0, 0, mv_frame_out_info.width, mv_frame_out_info.height, rgbBytes);

// 在图像回调中处理裁剪的代码片段
                            ImageProcessor processor = new ImageProcessor();

// 定义前端请求参数（示例：裁剪3个不同区域）
                            FrontendRequest request = new FrontendRequest();
// 参数含义：(大矩形起始X比例, 大矩形起始Y比例, 小矩形宽度比例, 小矩形高度比例)
                            request.addRectangleRatio(0.65, 0.55, 0.40, 0.15);  // 区域1：对应原逻辑的默认比例
                            request.addRectangleRatio(0.30, 0.20, 0.15, 0.20);  // 区域2：自定义比例（例如数字"1"）
                            request.addRectangleRatio(0.60, 0.20, 0.15, 0.20);  // 区域3：自定义比例（例如第二个"7"）

// 计算所有裁剪参数
                            List<ImageCutParam> cutParams = processor.calculateMultipleImageCutParams(rgbImage, request);

// 执行裁剪并处理结果
                            for (int i = 0; i < cutParams.size(); i++) {
                                ImageCutParam param = cutParams.get(i);
                                BufferedImage croppedImage = processor.cutImage(rgbImage, param);  // 使用统一裁剪方法

                                String croppedImageBase64 = processor.imageToBase64(croppedImage);

                                // 创建ModelInput对象
                                ModelInput modelInput = new ModelInput(croppedImageBase64, param);

                                // 添加额外信息
                                modelInput.addExtraInfo("region", "digit_" + (i + 1));
                                modelInput.addExtraInfo("timestamp", System.currentTimeMillis());

                                // 将处理后的数据发送给YOLO模型
                                LinkPython.linkPythonWithModelInput(modelInput);
                            }

                        } catch (Exception e) {
                            System.err.println("图片裁切处理失败: " + e.getMessage());
                            e.printStackTrace();
                        }


                        // 2. RGB转BufferedImage（使用TYPE_3BYTE_BGR容器，直接写入RGB像素）
                        BufferedImage image = new BufferedImage(
                                mv_frame_out_info.width,
                                mv_frame_out_info.height,
                                BufferedImage.TYPE_3BYTE_BGR // 容器类型不影响，关键是传入的rgbBytes通道顺序
                        );
                        // 将RGB像素数据写入图像（setDataElements会按数组顺序填充，确保R→G→B对应正确通道）
                        image.getRaster().setDataElements(0, 0, mv_frame_out_info.width, mv_frame_out_info.height, rgbBytes);

                        // 修改为：
// 1. 创建ImageProcessor实例
                        ImageProcessor processor = new ImageProcessor();

// 2. 定义裁切参数（根据图片内容调整）
                        FrontendRequest request = new FrontendRequest();
                        request.addRectangleRatio(0.4, 0.4, 0.35, 0.35);

// 3. 计算裁切参数
                        List<ImageCutParam> cutParams = processor.calculateMultipleImageCutParams(image, request);

// 4. 对每个裁切区域进行处理
                        for (int i = 0; i < cutParams.size(); i++) {
                            ImageCutParam param = cutParams.get(i);

                            // 裁剪图片
                            BufferedImage croppedImage = processor.cutImage(image, param);

// ===== 保存图片代码开始 =====（已注释，恢复时取消注释即可）

// 新增：保存裁切后的图片到本地（第二个裁切区域）
                            try {
                                String saveDir = "C:\\Users\\26487\\Desktop\\裁切图片";
                                String fileName = "region_second_" + (i + 1); // 区域编号作为文件名前缀
                                String savedPath = processor.saveCroppedImageWithTimestamp(croppedImage, saveDir, fileName);
                                System.out.println("裁切图片已保存到: " + savedPath);
                            } catch (Exception e) {
                                System.err.println("保存裁切图片失败: " + e.getMessage());
                                e.printStackTrace();
                            }

// ===== 保存图片代码结束 =====

                            // 转换为Base64格式
                            String croppedImageBase64 = null;
                            try {
                                croppedImageBase64 = processor.imageToBase64(croppedImage);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                            // 创建ModelInput对象
                            ModelInput modelInput = new ModelInput(croppedImageBase64, param);

                            // 添加额外信息
                            modelInput.addExtraInfo("region", "digit_" + (i + 1));
                            modelInput.addExtraInfo("timestamp", System.currentTimeMillis());

                            // 将处理后的数据发送给YOLO模型
                            LinkPython.linkPythonWithModelInput(modelInput);
                        }


                        // 3. 压缩为JPEG（复用原压缩方法，无需修改）
                        byte[] compressedBytes = compressImageToJPEG(image, 0.6f);

                        // 4. 发送压缩后的数据（保持原逻辑不变）
                        MvsWebSocket.sendFrames(camIndex, compressedBytes, mv_frame_out_info.width, mv_frame_out_info.height);

                    }
                    return 0;


                }
            });

            if (MV_OK != nRet) {
                System.err.printf("register image callback failed, errcode: [%#x]\n", nRet);
                break;
            }


// Turn off trigger mode
            nRet = MvCameraControl.MV_CC_SetEnumValueByString(hCamera, "TriggerMode", "Off");

// 设置帧率（例如设置为1帧/秒）
            nRet = MvCameraControl.MV_CC_SetFloatValue(hCamera, "AcquisitionFrameRate", 1);


            if (MV_OK != nRet) {
                System.err.printf("SetTriggerMode failed, errcode: [%#x]\n", nRet);
                break;
            }

            // 开始抓取
            nRet = MvCameraControl.MV_CC_StartGrabbing(hCamera);
            if (MV_OK != nRet) {
                System.err.printf("StartGrabbing failed, errcode: [%#x]\n", nRet);
                break;
            }
            synchronized (SDK_LOCK2) {
                // 循环检测连接
                while (!toCloseMVS) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                // 停止抓取和关闭设备需要同步保护
                nRet = MvCameraControl.MV_CC_StopGrabbing(hCamera);
                if (MV_OK != nRet) {
                    System.err.printf("StopGrabbing failed, errcode: [%#x]\n", nRet);
                    break;
                }

                // close device
                nRet = MvCameraControl.MV_CC_CloseDevice(hCamera);
                if (MV_OK != nRet) {
                    System.err.printf("CloseDevice failed, errcode: [%#x]\n", nRet);
                    break;
                }
            }

        } while (false);

        endMVS(nRet, hCamera);
    }

    /**
     * 结束MVS摄像头
     *
     * @param nRet
     * @param hCamera
     */
    private void endMVS(int nRet, Handle hCamera) {
        if (null != hCamera) {
            // Destroy handle
            System.out.println("hCamera" + hCamera);
            nRet = MvCameraControl.MV_CC_DestroyHandle(hCamera);
            if (MV_OK != nRet) {
                System.err.printf("DestroyHandle failed, errcode: [%#x]\n", nRet);
            }
        }
        MvCameraControl.MV_CC_Finalize();
    }

    /**
     * 关闭摄像头
     *
     * @param
     *
     */
    public void closeMVS() {
        this.toCloseMVS = true;
    }


    // 新增：BayerRG8转RGB的转换方法
    private byte[] convertBayerRG8ToRGB(byte[] bayerBytes, int width, int height, int bytesPerRow) {
        // 校验输入数据长度（BayerRG8总长度应为 高度×每行字节数）
        if (bayerBytes == null || bayerBytes.length != height * bytesPerRow) {
            throw new IllegalArgumentException("Bayer数据长度异常，预期: " + height * bytesPerRow + ", 实际: " + (bayerBytes == null ? 0 : bayerBytes.length));
        }

        int rgbLength = width * height * 3; // RGB格式每个像素3字节（R、G、B）
        byte[] rgbBytes = new byte[rgbLength];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // 计算Bayer数据中当前像素的索引（使用bytesPerRow确保行对齐）
                int bayerIndex = y * bytesPerRow + x;
                byte pixel = bayerBytes[bayerIndex];
                int val = pixel & 0xFF; // 转为无符号值（避免负数问题）

                // RGGB模式解析（Bayer阵列排列：R G / G B）
                boolean evenRow = (y % 2 == 0); // 偶数行（0、2、4...）
                boolean evenCol = (x % 2 == 0); // 偶数列（0、2、4...）
                int r = 0, g = 0, b = 0;

                if (evenRow) {
                    if (evenCol) {
                        // 位置：R（偶数行+偶数列）
                        r = val;
                        // G取右侧相邻像素（若边界则用当前R值）
                        g = (x + 1 < width) ? (bayerBytes[y * bytesPerRow + x + 1] & 0xFF) : val;
                        // B取右下对角像素（若边界则用当前R值）
                        b = (y + 1 < height && x + 1 < width) ? (bayerBytes[(y + 1) * bytesPerRow + x + 1] & 0xFF) : val;
                    } else {
                        // 位置：G（偶数行+奇数列）
                        g = val;
                        // R取左侧相邻像素（若边界则用当前G值）
                        r = (x - 1 >= 0) ? (bayerBytes[y * bytesPerRow + x - 1] & 0xFF) : val;
                        // B取下方相邻像素（若边界则用当前G值）
                        b = (y + 1 < height) ? (bayerBytes[(y + 1) * bytesPerRow + x] & 0xFF) : val;
                    }
                } else {
                    if (evenCol) {
                        // 位置：G（奇数行+偶数列）
                        g = val;
                        // R取上方相邻像素（若边界则用当前G值）
                        r = (y - 1 >= 0) ? (bayerBytes[(y - 1) * bytesPerRow + x] & 0xFF) : val;
                        // B取右侧相邻像素（若边界则用当前G值）
                        b = (x + 1 < width) ? (bayerBytes[y * bytesPerRow + x + 1] & 0xFF) : val;
                    } else {
                        // 位置：B（奇数行+奇数列）
                        b = val;
                        // G取左侧相邻像素（若边界则用当前B值）
                        g = (x - 1 >= 0) ? (bayerBytes[y * bytesPerRow + x - 1] & 0xFF) : val;
                        // R取左上对角像素（若边界则用当前B值）
                        r = (y - 1 >= 0 && x - 1 >= 0) ? (bayerBytes[(y - 1) * bytesPerRow + x - 1] & 0xFF) : val;
                    }
                }

                // 填充RGB数组（关键修改：通道顺序为 R→G→B）
                int rgbIndex = (y * width + x) * 3;
                rgbBytes[rgbIndex] = (byte) r;     // R通道
                rgbBytes[rgbIndex + 1] = (byte) g; // G通道
                rgbBytes[rgbIndex + 2] = (byte) b; // B通道
            }
        }
        return rgbBytes;
    }

    //将RGB转换为JPEG格式（无用）
    private byte[] compressImageToJPEG(BufferedImage image, float quality) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // 获取JPEG图像写入器
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            if (!writers.hasNext()) {
                throw new IllegalStateException("找不到JPEG图像写入器");
            }
            ImageWriter writer = writers.next();

            // 创建图像输出流
            try (ImageOutputStream imageOut = ImageIO.createImageOutputStream(out)) {
                writer.setOutput(imageOut);

                // 设置压缩参数
                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(quality); // 0.0~1.0，数值越小压缩率越高

                // 写入图像
                IIOImage iioImage = new IIOImage(image, null, null);
                writer.write(null, iioImage, param);
                writer.dispose(); // 释放资源
            }

            return out.toByteArray();
        } catch (Exception e) {
            System.err.println("图像压缩失败：" + e.getMessage());
            return new byte[0];
        }
    }

    // 移除连接
    private void removeLink(int index) {
        MvsWebSocket.getSessionMap().remove(index);
        MvsWebSocket.getGrabCallbackMap().remove(index);
        GasDataWebSocket.getSessionMap().remove(index);
    }
}