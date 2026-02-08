package com.en_chu.calculator_api_spring.controller;

import com.en_chu.calculator_api_spring.model.*;
import com.en_chu.calculator_api_spring.service.*;
import com.en_chu.calculator_api_spring.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User API", description = "使用者核心資料管理")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserCareerService userCareerService;

    @Autowired
    private UserLaborPensionService userLaborPensionService;

    @Autowired
    private UserLaborInsuranceService userLaborInsuranceService;

    @Autowired
    private UserTaxService userTaxService;

    @Operation(summary = "獲取當前使用者所有資料")
    @GetMapping("/me")
    public ResponseEntity<UserFullDataRes> getMe() {
        String uid = SecurityUtils.getCurrentUserUid();
        return ResponseEntity.ok(userService.getFullUserData(uid));
    }

    @Operation(summary = "更新使用者個人資料", description = "只接受 birthDate, gender, marriageYear, biography 等允許使用者修改的欄位。")
    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody @Valid UserProfileUpdateReq req) {
        String uid = SecurityUtils.getCurrentUserUid();
        userProfileService.updateProfile(uid, req);
        return ResponseEntity.ok("更新成功");
    }

    @Operation(summary = "更新使用者職涯資料")
    @PutMapping("/career")
    public ResponseEntity<String> updateCareer(@RequestBody @Valid UserCareerDto req) {
        String uid = SecurityUtils.getCurrentUserUid();
        userCareerService.updateCareer(uid, req);
        return ResponseEntity.ok("更新成功");
    }

    @Operation(summary = "更新使用者勞退資料")
    @PutMapping("/labor-pension")
    public ResponseEntity<String> updateLaborPension(@RequestBody @Valid UserLaborPensionDto req) {
        String uid = SecurityUtils.getCurrentUserUid();
        userLaborPensionService.updateLaborPension(uid, req);
        return ResponseEntity.ok("更新成功");
    }

    @Operation(summary = "更新使用者勞保資料")
    @PutMapping("/labor-insurance")
    public ResponseEntity<UserLaborInsuranceDto> update(@Valid @RequestBody UserLaborInsuranceDto req) {
        String uid = SecurityUtils.getCurrentUserUid();
        return ResponseEntity.ok(userLaborInsuranceService.updateLaborInsurance(uid, req));
    }



    @Operation(summary = "更新使用者稅務資料")
    @PutMapping("/tax")
    public ResponseEntity<String> updateTax(@RequestBody @Valid UserTaxDto req) {
        String uid = SecurityUtils.getCurrentUserUid();
        userTaxService.updateTax(uid, req);
        return ResponseEntity.ok("更新成功");
    }

    @Operation(summary = "刪除當前使用者帳號")
    @DeleteMapping
    public ResponseEntity<Void> deleteUser() {
        String uid = SecurityUtils.getCurrentUserUid();
        userService.deleteUser(uid);
        return ResponseEntity.noContent().build();
    }
}
