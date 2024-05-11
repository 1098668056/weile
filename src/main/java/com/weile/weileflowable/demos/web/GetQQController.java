package com.weile.weileflowable.demos.web;

import cn.hutool.core.text.PasswdStrength;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.PostVMInitHook;

import javax.websocket.server.PathParam;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: xwl
 * @Date: 2024/4/14 15:57
 * @Description:
 **/
@RestController
public class GetQQController {


    @GetMapping("/get/{QQ}")
    public JSON getQzone(@PathVariable("QQ")Integer uin){
        Map<String, Object> param = new HashMap<>();
        param.put("appid","1002");
        param.put("key","gbfMCvdNqq9c3UxJ");
        param.put("uin",uin);
        param.put("sign",MD5.create().digestHex("1002" + uin + "gbfMCvdNqq9c3UxJ"));
        String post = HttpUtil.post("http://dsapi.cccyun.cc/shuo.php", param);
        return JSONUtil.parseObj(UnicodeUtil.toString(post));

    }
}
