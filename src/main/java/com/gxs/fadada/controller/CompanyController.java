package com.gxs.fadada.controller;

import com.alibaba.fastjson.JSONArray;
import com.fadada.lightapi.FDDSDKClient;
import com.fadada.lightapi.base.ReturnBase;
import com.fadada.lightapi.beans.conformity.register.AccountRegisterReq;
import com.fadada.lightapi.beans.conformity.register.AccountRegisterRsp;
import com.fadada.lightapi.beans.conformity.verfiy.VerGetCompanyUrlReq;
import com.fadada.lightapi.beans.conformity.verfiy.VerGetUrlRsp;
import com.fadada.lightapi.beans.conformity.verfiy.info.AgentInfoReq;
import com.fadada.lightapi.beans.conformity.verfiy.info.CompanyInfoReq;
import com.fadada.lightapi.beans.signatureOperate.SignatureOperateReq;
import com.fadada.lightapi.beans.signatureOperate.SignatureOperateRsp;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

import static org.apache.commons.codec.digest.DigestUtils.md5;
import static org.apache.commons.codec.digest.DigestUtils.sha1;

@Controller
public class CompanyController {

    @Value("${fadada.appid}")
    String app_id;
    @Value("${fadada.appsecret}")
    String app_secret;

    /**
     * 先去法大大注册商户
     * @return
     */
    @RequestMapping("/register")
    @ResponseBody
    public String register(){
        FDDSDKClient client = new FDDSDKClient();
        AccountRegisterReq req = new AccountRegisterReq();
        req.setOpenId("123456"); //对应fupin832的cust_no
        req.setAccountType(2); //用户类型：1-个人，2-企业
        AccountRegisterRsp rsp = client. register (req);
        System.out.println(rsp.toString());
        return rsp.toString();
    }

    /**
     * 吊起实名认证页面
     * @return
     */
    @RequestMapping("/getCompanyUrl")
    @ResponseBody
    public String getCompanyUrl(){
        FDDSDKClient client= new FDDSDKClient();
        VerGetCompanyUrlReq company = new VerGetCompanyUrlReq();
        company.setCustomerId("FDFDB79FB42EE60ACA8B00CE4F915200");
        company.setPageModify("2");
//        company.setVerifiedWay("0");
        company.setNotifyUrl("http://www.baidu.com");
//        company.setCompanyPrincipalType("2");
//        CompanyInfoReq companyInfoReq = new CompanyInfoReq();
//        companyInfoReq.setCompany_name("注册公司");
//        companyInfoReq.setCredit_image_path("https://restest.fupin832.com/CgF4JlzSTOCAIF-FAABoD6GJ06Y142.jpg");
//        company.setCompanyInfo(companyInfoReq);
//        AgentInfoReq agentInfoReq = new AgentInfoReq();
//        agentInfoReq.setAgent_id("4343");
//        agentInfoReq.setAgent_id_front_path("https://restest.fupin832.com/CgF4JlzSTOCAIF-FAABoD6GJ06Y142.jpg");
//        agentInfoReq.setAgent_mobile("18703037819");
//        agentInfoReq.setAgent_name("代理人");
//        company.setAgentInfo(agentInfoReq);
        VerGetUrlRsp rsp= client.verGetCompanyUrl(company);
        System.out.println(rsp.toString());
        return rsp.toString();
    }

    /**
     * 获取签章图片
     * @return
     */
    @RequestMapping("/querySignature")
    @ResponseBody
    public String querySignature(){
        String customer_id = "FDFDB79FB42EE60ACA8B00CE4F915200";

        FDDSDKClient client= new FDDSDKClient ();
        ReturnBase base = client.applyCert(customer_id, "0d054ce5705b4d59a093202342384f31");

        SignatureOperateReq sign = new SignatureOperateReq();
        sign.setCustomerId(customer_id);
        String timestamp = "20200401185111";
        String signature_id = "";
        byte[] img = sha1(app_id+md5(timestamp).toString()+sha1(app_secret+ customer_id+ signature_id).toString());
        String  imgBase = new BASE64Encoder().encode(img);
        sign.setSignatureImgBase64(imgBase);

        SignatureOperateRsp rsp = client.querySignature(sign);
        JSONArray json = (JSONArray)rsp.getData();
        Base64ToImage(json.getJSONObject(0).get("signature_img_base64").toString(),"C:\\abc.jpg");
        return rsp.toString();

    }

    /**
     * base64字符串转换成图片 (对字节数组字符串进行Base64解码并生成图片)
     * @param imgStr		base64字符串
     * @param imgFilePath	指定图片存放路径  （注意：带文件名）
     * @return
     */
    public boolean Base64ToImage(String imgStr,String imgFilePath) {

        if (StringUtils.isEmpty(imgStr)) // 图像数据为空
            return false;

        try {
            // Base64解码
            byte[] b = Base64Utils.decodeFromString(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }

            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }


}
