//package com.weile.weileflowable.demos.web;
//
//import cn.hutool.http.HttpResponse;
//import cn.hutool.http.HttpUtil;
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @Author: xwl
// * @Date: 2024/4/20 17:24
// * @Description:
// **/
//@RestController
//public class SyncController {
//    private static  String  generateOrderUrl = "https://{siteUrl}/getPayInterface";
//    private static  String  verifyOrderUrl = "https://{siteUrl}/pay/getAliPay";
//    private static  String Cookie = "";
//    private   String Authorization = "";
//
//    private static String initUrl = "https://{siteUrl}/initGeetest";
//    private static String loginUrl ="https://{siteUrl}/frontLogin";
//    private static String skipUrl  ="跳过GEETEST平台";
//
//    @GetMapping("/pay/{orderNo}")
//    public Object payment(@PathVariable String orderNo){
//        Map<String,Object> param = new HashMap<>();
//        param.put("payType",1);
//        param.put("payId",6);
//        param.put("totalMoney",1);
//        HttpResponse resp = HttpUtil.createPost(generateOrderUrl).header("Cookie", Cookie)
//                .header("Authorization", Authorization).form(param).execute();
//        String body = resp.body();
//
//        //判断用户是否状态
//        JSONObject respCode = JSONUtil.parseObj(body);
//        //如果没有登录则代理对应的界面
//        if ((Integer) respCode.get("code") == -2){
//            //循环获取验证码通过率并不是很很高
//            int flag = 0;
//            String[] result =null;
//            while (flag != 2 ){
//                //需要模拟登录获取的token
//                String jsonStr = HttpUtil.createPost(initUrl).execute().body();
//                JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
//                String challenge = (String)jsonObject.get("challenge");
//                String gt = (String) jsonObject.get("gt");
//                //开始跳过验证
//                String codeUrl = skipUrl + "&gt=" + gt + "&challenge=" + challenge;
//                String captchaAndChallenge = HttpUtil.createGet(codeUrl).timeout(60000).execute().body();
//                result = captchaAndChallenge.split("\\|");
//                flag = result.length;
//            }
//            //开始登录网站
//            Map<String,Object>  loginParam = new HashMap<>();
//            loginParam.put("passWord","11111111111");
//            loginParam.put("userName","11111111111");
//            loginParam.put("geetest_challenge",result[0]);
//            loginParam.put("geetest_validate",result[1]);
//            loginParam.put("geetest_seccode",result[1]+"|jordan");
//            String authInfo = HttpUtil.createPost(loginUrl).form(loginParam).execute().body();
//            JSONObject userInfo = JSONUtil.parseObj(authInfo);
//            Object token = userInfo.get("token");
//            JSONObject authCode = JSONUtil.parseObj(token);
//            Authorization = (String) authCode.get("authCode");
//        }
//        String key = (String)respCode.get("key");
//        String orderNum = (String) respCode.get("orderNum");
//        //发起验证
//        Map<String,Object> orderParam = new HashMap<>();
//        orderParam.put("tranNo",orderNo);
//        orderParam.put("orderNum",orderNum);
//        orderParam.put("money",1111);
//        orderParam.put("key",key);
//        System.out.println("Authorization = " + Authorization);
//        HttpResponse respOrder = HttpUtil.createPost(verifyOrderUrl).header("Cookie", Cookie)
//                .header("Authorization", Authorization).form(orderParam).execute();
//        return respOrder.body();
//    }
//}
