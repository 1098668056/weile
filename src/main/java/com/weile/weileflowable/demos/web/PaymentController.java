package com.weile.weileflowable.demos.web;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xwl
 * @Date: 2024/4/20 17:24
 * @Description:
 **/
@RestController
public class PaymentController {
    private static  String  generateOrderUrl = "https://1.jw95.cn/pay/getPayInterface";
    private static  String  verifyOrderUrl = "https://1.jw95.cn/pay/getAliPay";
    private static  String Cookie = "frontUserInfo=48207e139fc3105acf77cfe75ba9acb1b4356fe87bd566760c58416014efd1732415412c8f9e4033df32b2a7bf16d2b8988688375d0dc4590710f6072e90a1388ffaeecfa11b0a461c6fb1d8be25a3fd7f885b3b2e2ae054b52503588359dcf66f7f9a1e232b62ec0ef63af79c8da407d991bae06547cf8076e4b4f3c06e6e952d7ddcf451ec1fbfbbf148a3ecd2a23f7f8ae069efcf757daeef9cf1174acad3451b6de5b6a5b40ba9436d7fc7701f285daa85e9061a3bd96aedc931f915360f777af4ae53b24616fb95a6368262081fd754fa3d2559d75f9438d60d6829bef39ace7a38f65ec141a9ad8b32e8afd51b21fe346816e316cf5af9ae5e54cbda8eae6803318138abb4c3152f4a945a62fd2d1742a12e4855f17e2534886d43dec681d29cefb39f71112490ffa794fc9450d3627a380f292232bd629cf252c39e873e0b09086fb5d85d724fc722bfbdc1284ae512c518d1d80d2a1a5a0c22a8f0c2e87bd299d10c1a5d3b1b43634f261918a773f10a4320a9464102058f848e4e8d940a996454aebf1276fc488845cf1de9a8cac5af0579ecd1ce9814a9ed3eb14d77b42ea9994863d7e55a8853be56205f9a7b9a3bdf6816c4d76f102bea3b53c30909be8c5f60bcf98a650b3389688cb9c0568c5c48239b8b123080f262ba959c1b158f7ab9293a7697be2f6ad87bb4ada8f964e862fe352eb3a47d374ccf65d80048309f28aaf2e2003b4bdfe9f4e5a4ef6b47898b38b7fa6be0d770e094e9986e5f905dcb12f22447c36813a0b5c22d09d4d76e7f541d739126e2d3547486d6bf38914ed0962ec0757522728a153f8a2f515b9ae7b30183b4fb0d9b12d3e3b8fc3875669f9cf563bee5193b0ce528303a65a9b8c69dd2a73c31f387b41fd1b3f008ed0aa668e08089b4b555dbf9e2f171ea1648a7656b5c46c0de3e6125617becd73d9603121883badecfaf88e7a90ba4a9f43d1da736311687a4a58fd958dcd74b686ab6739d2b43ccf5839eee3697653c89526331951c9b0d115e5c9a084c56e8b9eb506ede66e1d6225c5f8569b4392928df4d03fc0411cb671506ba09f7";
    private   String Authorization = "558e53fe42589de04ee6dfe11f8087ad";

    private static String initUrl = "https://1.jw95.cn/initGeetest";
    private static String loginUrl ="https://1.jw95.cn/frontLogin";
    private static String skipUrl  ="http://api.damagou.top/apiv1/jiyanRecognize.html?userkey=a402da45aa6135a5467ebe2ee8bb1bce";

    @GetMapping("/pay/{orderNo}")
    public Object payment(@PathVariable String orderNo){
        Map<String,Object> param = new HashMap<>();
        param.put("payType",1);
        param.put("payId",6);
        param.put("totalMoney",1);
        HttpResponse resp = HttpUtil.createPost(generateOrderUrl).header("Cookie", Cookie)
                .header("Authorization", Authorization).form(param).execute();
        String body = resp.body();

        //判断用户是否状态
        JSONObject respCode = JSONUtil.parseObj(body);
        //如果没有登录则代理对应的界面
        if ((Integer) respCode.get("code") == -2){
            //循环获取验证码通过率并不是很很高
            int flag = 0;
            String[] result =null;
            while (flag != 2 ){
                //需要模拟登录获取的token
                String jsonStr = HttpUtil.createPost(initUrl).execute().body();
                JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
                String challenge = (String)jsonObject.get("challenge");
                String gt = (String) jsonObject.get("gt");
                //开始跳过验证
                String codeUrl = skipUrl + "&gt=" + gt + "&challenge=" + challenge;
                String captchaAndChallenge = HttpUtil.createGet(codeUrl).timeout(60000).execute().body();
                result = captchaAndChallenge.split("\\|");
                flag = result.length;
            }
            //开始登录网站
            Map<String,Object>  loginParam = new HashMap<>();
            loginParam.put("passWord","123123");
            loginParam.put("userName","test123123");
            loginParam.put("geetest_challenge",result[0]);
            loginParam.put("geetest_validate",result[1]);
            loginParam.put("geetest_seccode",result[1]+"|jordan");
            String authInfo = HttpUtil.createPost(loginUrl).form(loginParam).execute().body();
            JSONObject userInfo = JSONUtil.parseObj(authInfo);
            Object token = userInfo.get("token");
            JSONObject authCode = JSONUtil.parseObj(token);
            Authorization = (String) authCode.get("authCode");
        }
        String key = (String)respCode.get("key");
        String orderNum = (String) respCode.get("orderNum");
        //发起验证
        Map<String,Object> orderParam = new HashMap<>();
        orderParam.put("tranNo",orderNo);
        orderParam.put("orderNum",orderNum);
        orderParam.put("money",1111);
        orderParam.put("key",key);
        System.out.println("Authorization = " + Authorization);
        HttpResponse respOrder = HttpUtil.createPost(verifyOrderUrl).header("Cookie", Cookie)
                .header("Authorization", Authorization).form(orderParam).execute();
        return respOrder.body();
    }
}
