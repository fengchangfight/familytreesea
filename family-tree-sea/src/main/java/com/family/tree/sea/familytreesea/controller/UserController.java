package com.family.tree.sea.familytreesea.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.family.tree.sea.familytreesea.config.PathConstants;
import com.family.tree.sea.familytreesea.entity.User;
import com.family.tree.sea.familytreesea.model.*;
import com.family.tree.sea.familytreesea.service.FamilyTreeService;
import com.family.tree.sea.familytreesea.service.UserService;
import com.family.tree.sea.familytreesea.utils.LicenseUtil;
import com.family.tree.sea.familytreesea.utils.SecurityUtil;
import com.family.tree.sea.familytreesea.utils.Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.RenderedImage;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(PathConstants.USER)
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private Environment env;

    @Autowired
    private UserService userService;

    @Autowired
    private FamilyTreeService familyTreeService;

    @Autowired
    private LicenseUtil licenseUtil;

    private String currentUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return currentUserName;
        }else{
            return null;
        }
    }

    @RequestMapping(value = "/profile-image", method = RequestMethod.GET)
    public String getProfile(){
        return userService.generateUserProfileUrlByUsername(Util.currentUserName(), "fts-user-icon", "manicon_small.png");
    }

    @RequestMapping(value="/whoami", method = RequestMethod.GET)
    public FamilyResponse whoami(){
        FamilyResponse ret = new FamilyResponse();

        String username = currentUserName();
        User user = userService.findByUsername(username);
        Map<String, Object> mp = new HashMap<>();
        ret.setOk(true);
        mp.put("uid", user.getId());
        if(StringUtils.isNotEmpty(user.getNickname())){
            mp.put("nickname", user.getNickname());
            ret.setData(mp);
            ret.setMessage("成功获取我是谁");
            return ret;
        }
        mp.put("nickname", username);
        ret.setData(mp);
        ret.setMessage("成功获取我是谁");
        return ret;
    }


    @RequestMapping(value="/user-info", method = RequestMethod.GET)
    public UserVO getUserVO(){
        String username = currentUserName();
        User user = userService.findByUsername(username);
        String profileImage = userService.generateUserProfileUrlByUsername(username, "fts-user-profile", "manicon.png");

        return new UserVO(user.getId(), user.getNickname(), user.getPhone(), user.getEmail(), profileImage);
    }


    @RequestMapping(value="/user-info", method = RequestMethod.POST)
    public void setUserInfoBasic(@RequestBody String params){
        JSONObject job = (JSONObject) JSON.parse(params);
        String nickName = job.getString("nick_name");
        String phone = job.getString("phone");
        String email = job.getString("email");

        String username = currentUserName();

        User user = userService.findByUsername(username);
        user.setNickname(nickName);
        user.setPhone(phone);
        user.setEmail(email);

        userService.save(user);
    }


    @RequestMapping(value="/remaining-ft-balance", method = RequestMethod.GET)
    public FamilyResponse balance(){
        FamilyResponse ret = new FamilyResponse();
        String whoami = currentUserName();
        User user = userService.findByUsername(whoami);
        Long created = familyTreeService.countFamilyTreeCreatedByUser(user);
        String licenseCode = user.getLicenseCode();
        Map<String,String> licenseContent=licenseUtil.parseLicenseCode(licenseCode);
        int maxCount = Integer.valueOf(licenseContent.get("max_family_count"));
        int balance = maxCount-created.intValue();
        Map<String,Integer> mp = new HashMap<>();
        mp.put("max", maxCount);
        mp.put("balance", balance);
        ret.setMessage("");
        ret.setOk(true);
        ret.setData(mp);
        return ret;
    }

    @RequestMapping(value="/by-email-or-phone/{param}/", method = RequestMethod.GET)
    public FamilyResponse queryUsersByUserName(@PathVariable("param") String param){
        FamilyResponse ret = new FamilyResponse();

        // here username could be phone, email or nickName

        User userByPhone = userService.findUserByPhoneExcludeMe(param, Util.currentUserName());
        User userByEmail = userService.findUserByEmailExcludeMe(param, Util.currentUserName());

        User user = userByPhone==null?userByEmail:userByPhone;

        ret.setOk(true);
        if(user!=null){
            UserSelectVO userSelectVO = new UserSelectVO(user.getId(), user.getNickname(), userService.generateUserProfileUrlByUsername(user.getUsername(),"fts-user-icon", "manicon_small.png"));
            ret.setMessage("成功查询到用户");
            ret.setData(userSelectVO);
        }else{
            ret.setMessage("没有查询到用户");
            ret.setData(null);
        }
        return ret;


    }

    @RequestMapping(value = "/icon-by-file-name/{id:.+}/", method = RequestMethod.GET)
    public FamilyResponse getImageBase64(@PathVariable("id") String fileName){
        FamilyResponse ret = new FamilyResponse();
        if(StringUtils.isEmpty(fileName)){
            ret.setOk(false);
            ret.setMessage("图文文件名为空");
            return ret;
        }
        InputStream imageIn = getClass().getClassLoader().getResourceAsStream("static/imgs/"+fileName);
        RenderedImage manRenderedImage= null;
        ByteArrayOutputStream imageBaos = new ByteArrayOutputStream();
        try {
            manRenderedImage = ImageIO.read(imageIn);
            ImageIO.write(manRenderedImage, "png", imageBaos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String manString = "data:image/png;base64," + DatatypeConverter.printBase64Binary(imageBaos.toByteArray());

        User user = userService.findByUsername(Util.currentUserName());

        ret.setOk(true);
        ret.setData(manString);
        ret.setMessage("成功获得图片base64");
        return ret;

    }

    @RequestMapping(value="/default-human-icon", method = RequestMethod.GET)
    public Map<String,String> base64ManAndWomanImage(){
        Map<String,String> result = new HashMap<>();
        ByteArrayOutputStream manbaos = new ByteArrayOutputStream();
        ByteArrayOutputStream womanbaos = new ByteArrayOutputStream();
        try {
            InputStream manin = getClass().getClassLoader().getResourceAsStream("static/imgs/shusheng.png");
            InputStream womanin = getClass().getClassLoader().getResourceAsStream("static/imgs/nvhai.png");

            RenderedImage manRenderedImage=javax.imageio.ImageIO.read(manin);
            RenderedImage womanRenderedImage=javax.imageio.ImageIO.read(womanin);
            ImageIO.write(manRenderedImage, "png", manbaos);
            ImageIO.write(womanRenderedImage, "png", womanbaos);
        } catch (IOException e) {
            throw new RuntimeException("=====Error reading man icon file=====");
        }
        String manString = "data:image/png;base64," + DatatypeConverter.printBase64Binary(manbaos.toByteArray());
        String womanString = "data:image/png;base64," + DatatypeConverter.printBase64Binary(womanbaos.toByteArray());

        result.put("man_icon", manString);
        result.put("woman_icon", womanString);
        return result;
    }

    @RequestMapping(value = "/profile-upload", method = RequestMethod.POST)
    public FamilyResponse uploadProfile(@RequestParam("img") MultipartFile uploadFile){
        FamilyResponse ret = new FamilyResponse();

        String profile = String.join("", env.getActiveProfiles());

        String key =  "ft_profile_"+profile+"_"+Util.currentUserName()+"_header.jpg";

        try {
            byte[] profileBytes = uploadFile.getBytes();
            String USER_PROFILE_BUCKET_NAME = env.getProperty("aliyun.user.profile.bucket");
            String endpoint = env.getProperty("aliyun.oss.endpoint");
            String accessKeyId = env.getProperty("family.tree.root.access.key.id");
            String accessKeySecret = env.getProperty("family.tree.root.access.key.secret");
//            OSSClient ossClient = new OSSClient(endpoint, accessKeyId,accessKeySecret);
//
//            ossClient.putObject(USER_PROFILE_BUCKET_NAME, key, new ByteArrayInputStream(profileBytes));
//            ossClient.shutdown();
            ret.setOk(true);
            ret.setData("what the hell");
            return ret;
        } catch (IOException e) {
            LOG.error("=============="+e.getMessage()+"========");
            return null;
        }
    }


    @RequestMapping(value = "/password", method = RequestMethod.POST)
    public FamilyResponse updatePassword(@RequestBody String params){
        FamilyResponse ret = new FamilyResponse();

        JSONObject job = (JSONObject)JSON.parse(params);
        String oldPassword = job.getString("old_password");
        String newPassword = job.getString("new_password");
        // check if old password is correct, if not return with error message
        User user = userService.findByUsername(Util.currentUserName());
        try {
            String encPass = SecurityUtil.generateStorngPasswordHash(oldPassword, user.getSalt());
            if(!user.getPassword().equals(encPass)){
                ret.setOk(false);
                ret.setMessage("旧密码不正确");
                return ret;
            }
        } catch (NoSuchAlgorithmException e) {
            ret.setOk(false);
            ret.setMessage("无此算法错误");
            return ret;
        } catch (InvalidKeySpecException e) {
            ret.setOk(false);
            ret.setMessage("Key不合法错误");
            return ret;
        }
        // otherwise update with new password
        try {
            String encNewPassword = SecurityUtil.generateStorngPasswordHash(newPassword, user.getSalt());
            user.setPassword(encNewPassword);
            userService.save(user);
            ret.setOk(true);
            ret.setMessage("密码更新成功");
            return ret;
        } catch (NoSuchAlgorithmException e) {
            ret.setOk(false);
            ret.setMessage("无此算法错误2");
            return ret;
        } catch (InvalidKeySpecException e) {
            ret.setOk(false);
            ret.setMessage("Key不合法错误2");
            return ret;
        }
    }

    private static Date string2Date(String dateStr, SimpleDateFormat simpleDateFormat){
        Calendar cal = new GregorianCalendar();
        try {
            Date dt = simpleDateFormat.parse(dateStr);
            return dt;
        } catch (ParseException e) {
            LOG.error("Cannot parse date string...");
            return null;
        }

    }
    @GetMapping("/license-check")
    public FamilyResponse checkMyLicense(){
        FamilyResponse result = new FamilyResponse();
        User user = userService.findByUsername(Util.currentUserName());

        String licenseCode = user.getLicenseCode();

        try{
            Map<String,String> licenseContent = licenseUtil.parseLicenseCode(licenseCode);

            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date dt = string2Date(licenseContent.get("end_date"), sf);
            Date now = new Date();
            if(!checkExpire(dt, now)){
                result.setOk(true);
                result.setMessage("Succeed parsing license...");
                result.setData(licenseContent);
            }else{
                result.setOk(false);
                result.setMessage("License expired...");
                result.setData(licenseContent);
            }
        }catch (RuntimeException e){
            result.setOk(false);
            result.setMessage("Invalid license");
        }

        return result;
    }

    @PostMapping("/license-update")
    public FamilyResponse updateLicense(@RequestBody String params){
        FamilyResponse result = new FamilyResponse();

        JSONObject job = (JSONObject) JSON.parse(params);
        if(job.getString("license_code")==null || job.getString("license_code").length()<1){
            result.setOk(false);
            result.setMessage("Empty license code");
            result.setData(null);
        }

        String licenseCode = job.getString("license_code");

        try{
            // do the license check
            Map<String,String> licenseContent = licenseUtil.parseLicenseCode(licenseCode);
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            Date dt = string2Date(licenseContent.get("end_date"), sf);
            Date now = new Date();
            if(!checkExpire(dt, now)){
                // do nothing
            }else{
                // expire
                result.setOk(false);
                result.setMessage("这是过期的序列号");
                result.setData(licenseContent);
                return result;
            }
        }catch (RuntimeException e){
            result.setOk(false);
            System.out.println("what the hell");
            LOG.error("error what the hell");
            result.setMessage("不合法的序列号:"+e.getMessage());
            return result;
        }

        User user = userService.findByUsername(Util.currentUserName());
        user.setLicenseCode(licenseCode);

        userService.save(user);

        result.setOk(true);
        result.setMessage("成功更新序列号");
        result.setData(user.getId());

        return result;
    }

    private boolean checkExpire(Date dt, Date now){
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 1);
        Date toComp = c.getTime();
        if(toComp.compareTo(now)==1){
            // not expire
            return false;
        }else{
            // expire
            return true;
        }
    }


}
