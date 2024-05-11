package com.weile.weileflowable.demos.web;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xwl
 * @Date: 2024/4/21 21:47
 * @Description:
 **/
@RestController
public class KaKaYunController {
    private static String orderPOST = "http://wanmengpt.cn/home/user/uspay.html";
    private static String siteUrl = "http://wanmengpt.cn";
//    private static  String cookie = "tokenid=GNEvd788WORnTz6cbJS6czyTtm1BcVblZqH9sLuOVE3knJ1j7bzr6iAXpTqbuleN; PHPSESSID=u02g3qesmo3leipjhe6c7a5m12; Hm_lvt_0fedcf6d0ed762d5deae4c1e4f25fca1=1713682595,1713701671,1713755341; usertoken=b126a70c49168dea8c8a839f31c2b84e; Hm_lpvt_0fedcf6d0ed762d5deae4c1e4f25fca1=1713764376";
    private static  String cookie = "usertoken=b126a70c49168dea8c8a839f31c2b84e";
//    private static  String cookie = "tokenid=GNEvd788WORnTz6cbJS6czyTtm1BcVblZqH9sLuOVE3knJ1j7bzr6iAXpTqbuleN; PHPSESSID=tq11auhvgq1mgq3vfj16ljbnb2; Hm_lvt_0fedcf6d0ed762d5deae4c1e4f25fca1=1713849779,1713944671,1714011860,1714361275; usertoken=b126a70c49168dea8c8a839f31c2b84e; noticecookie=1; Hm_lpvt_0fedcf6d0ed762d5deae4c1e4f25fca1=1714371940";
    private static String skipUrl  ="http://api.damagou.top/apiv1/jiyanRecognize.html?userkey=a402da45aa6135a5467ebe2ee8bb1bce";

    private static String loginUrl= "http://wanmengpt.cn/home/user/index.html";
    @GetMapping("/ka/{num}")
    public ResponseEntity<String> payment(@PathVariable Integer num){
        Map<String,Object> param = new HashMap<>();
        param.put("passagewayid",5151);
        param.put("paymoney",num);
        HttpRequest response1 = HttpUtil.createPost(orderPOST).form(param).cookie(cookie);
        response1.header("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36");
        response1.header("Sec-Ch-Ua-Platform","Windows");
        response1.header("Origin","http://wanmentpt.cn");
        response1.header("Referer","http://wanmentpt.cn");

        HttpResponse response = response1.execute();

//        HttpResponse response = HttpUtil.createPost(orderPOST).form(param).cookie(cookie).execute();
        // 将HttpResponse转换为ResponseEntity
        // 获取HttpResponse的响应头
        HttpHeaders headers = new HttpHeaders();
        System.out.println("----------------wwwwwwww-------开始");
        response.headers().forEach((k,v)->{
            System.out.println("k = " + k);
            System.out.println("v = " + v);
            if (k !=null){
                if (k.equals("Location")){
                    headers.add(k,siteUrl+v.toString().replace("[","").replace("]",""));
                }else {
                    headers.add(k,v.toString().replace("[","").replace("]",""));
                }
            }
        });


        ResponseEntity<String> responseEntity = ResponseEntity
                .status(response.getStatus())
                .headers(headers)
                .body(response.body());
        return responseEntity;
    }
    public void setCookie(){
        //循环获取验证码通过率并不是很很高
        int flag = 0;
        String[] result =null;
        while (flag != 2 ) {
            String code = HttpUtil.get("http://wanmengpt.cn/home/geetest/StartCaptchaServlet");
            JSONObject jsonObject = JSONUtil.parseObj(code);
            String gt = (String) jsonObject.get("gt");
            String challenge = (String) jsonObject.get("challenge");
            //开始跳过验证
            String codeUrl = skipUrl + "&gt=" + gt + "&challenge=" + challenge;
            String captchaAndChallenge = HttpUtil.createGet(codeUrl).timeout(60000).execute().body();
            result = captchaAndChallenge.split("\\|");
            flag = result.length;
        }
        //开始登录获取token
        //开始登录网站
        Map<String,Object>  loginParam = new HashMap<>();
        loginParam.put("password","1098668056");
        loginParam.put("account","1098668056");
        loginParam.put("former_url","http://wanmengpt.cn/home/user/uspay.html");
        loginParam.put("geetest_challenge",result[0]);
        loginParam.put("geetest_validate",result[1]);
        loginParam.put("geetest_seccode",result[1]+"|jordan");
        String authInfo = HttpUtil.createPost(loginUrl).form(loginParam).execute().body();
    }

    public static void main(String[] args) {
        int flag = 0;
        String[] result =null;
        while (flag != 2 ) {
            String code = HttpUtil.get("http://wanmengpt.cn/home/geetest/StartCaptchaServlet");
            JSONObject jsonObject = JSONUtil.parseObj(code);
            String gt = (String) jsonObject.get("gt");
            String challenge = (String) jsonObject.get("challenge");
            //开始跳过验证
            String codeUrl = skipUrl + "&gt=" + gt + "&challenge=" + challenge;
            String captchaAndChallenge = HttpUtil.createGet(codeUrl).timeout(60000).execute().body();
            result = captchaAndChallenge.split("\\|");
            flag = result.length;

        }
        System.out.println("result[0] = " + result[0]);
        System.out.println("result[1] = " + result[1]);
    }
}
