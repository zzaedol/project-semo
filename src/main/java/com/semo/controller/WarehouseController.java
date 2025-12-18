package com.semo.controller;

import com.semo.dto.member.WarehouseDTO;
import com.semo.dto.member.WarehouseRegisterDTO;
import com.semo.security.service.CustomerUserDetails;
import com.semo.service.WarehouseService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/warehouse")
@RequiredArgsConstructor
@Slf4j
public class WarehouseController {

  private final WarehouseService warehouseService;

  /**
   * 창고 목록 페이지 (내 창고 목록)
   */
  @GetMapping("/list")
  public String listPage(@AuthenticationPrincipal CustomerUserDetails userDetails, Model model) {
    // 테스트용: 로그인하지 않은 경우 기본 사용자 ID 사용
    Long memberId = (userDetails != null) ? userDetails.getMemberId() : 1L;
    log.info("창고 목록 페이지 요청 - 사용자 ID: {}", memberId);

    List<WarehouseDTO> warehouses = warehouseService.getWarehousesByOwnerId(memberId);
    model.addAttribute("warehouses", warehouses);

    return "warehouse/list";
  }

  /**
   * 창고 등록 페이지
   */
  @GetMapping("/register")
  public String registerPage(Model model) {
    log.info("창고 등록 페이지 요청");
    model.addAttribute("warehouse", new WarehouseRegisterDTO());
    return "warehouse/register";
  }

  /**
   * 창고 등록 처리
   */
  @PostMapping("/register")
  public String registerWarehouse(
      @Valid @ModelAttribute("warehouse") WarehouseRegisterDTO dto,
      BindingResult bindingResult,
      @RequestParam(value = "image", required = false) MultipartFile image,
      @AuthenticationPrincipal CustomerUserDetails userDetails,
      RedirectAttributes redirectAttributes
  ) {
    // 테스트용: 로그인하지 않은 경우 기본 사용자 ID 사용
    Long memberId = (userDetails != null) ? userDetails.getMemberId() : 1L;
    log.info("창고 등록 처리 - 사용자 ID: {}, 창고명: {}", memberId, dto.getTitle());

    if (bindingResult.hasErrors()) {
      log.warn("창고 등록 유효성 검증 실패: {}", bindingResult.getAllErrors());
      redirectAttributes.addFlashAttribute("error", "입력 정보를 확인해주세요.");
      return "redirect:/warehouse/register";
    }

    try {
      warehouseService.registerWarehouse(dto, memberId, image);
      redirectAttributes.addFlashAttribute("success", "창고가 성공적으로 등록되었습니다.");
      return "redirect:/warehouse/list";
    } catch (Exception e) {
      log.error("창고 등록 실패", e);
      redirectAttributes.addFlashAttribute("error", "창고 등록에 실패했습니다: " + e.getMessage());
      return "redirect:/warehouse/register";
    }
  }

  /**
   * 창고 수정 페이지
   */
  @GetMapping("/edit/{id}")
  public String editPage(@PathVariable("id") Long warehouseId, Model model, RedirectAttributes redirectAttributes) {
    log.info("창고 수정 페이지 요청 - 창고 ID: {}", warehouseId);

    try {
      WarehouseDTO warehouse = warehouseService.getWarehouseById(warehouseId);

      // DTO를 RegisterDTO로 변환
      WarehouseRegisterDTO dto = WarehouseRegisterDTO.builder()
          .title(warehouse.getTitle())
          .description(warehouse.getDescription())
          .address(warehouse.getAddress())
          .areaSqm(warehouse.getAreaSqm())
          .pricePerMonth(warehouse.getPricePerMonth())
          .availableStatus(warehouse.getAvailableStatus())
          .latitude(warehouse.getLatitude())
          .longitude(warehouse.getLongitude())
          .imagePath(warehouse.getImagePath())
          .build();

      model.addAttribute("warehouse", dto);
      model.addAttribute("warehouseId", warehouseId);
      return "warehouse/edit";
    } catch (Exception e) {
      log.error("창고 조회 실패", e);
      redirectAttributes.addFlashAttribute("error", "창고 정보를 불러오는데 실패했습니다.");
      return "redirect:/warehouse/list";
    }
  }

  /**
   * 창고 수정 처리
   */
  @PostMapping("/edit/{id}")
  public String updateWarehouse(
      @PathVariable("id") Long warehouseId,
      @Valid @ModelAttribute("warehouse") WarehouseRegisterDTO dto,
      BindingResult bindingResult,
      @RequestParam(value = "image", required = false) MultipartFile image,
      RedirectAttributes redirectAttributes
  ) {
    log.info("창고 수정 처리 - 창고 ID: {}", warehouseId);

    if (bindingResult.hasErrors()) {
      log.warn("창고 수정 유효성 검증 실패: {}", bindingResult.getAllErrors());
      redirectAttributes.addFlashAttribute("error", "입력 정보를 확인해주세요.");
      return "redirect:/warehouse/edit/" + warehouseId;
    }

    try {
      warehouseService.updateWarehouse(warehouseId, dto, image);
      redirectAttributes.addFlashAttribute("success", "창고 정보가 수정되었습니다.");
      return "redirect:/warehouse/list";
    } catch (Exception e) {
      log.error("창고 수정 실패", e);
      redirectAttributes.addFlashAttribute("error", "창고 수정에 실패했습니다: " + e.getMessage());
      return "redirect:/warehouse/edit/" + warehouseId;
    }
  }

  /**
   * 창고 삭제 처리
   */
  @PostMapping("/delete/{id}")
  public String deleteWarehouse(
      @PathVariable("id") Long warehouseId,
      @AuthenticationPrincipal CustomerUserDetails userDetails,
      RedirectAttributes redirectAttributes
  ) {
    // 테스트용: 로그인하지 않은 경우 기본 사용자 ID 사용
    Long memberId = (userDetails != null) ? userDetails.getMemberId() : 1L;
    log.info("창고 삭제 처리 - 창고 ID: {}, 사용자 ID: {}", warehouseId, memberId);

    try {
      warehouseService.deleteWarehouse(warehouseId, memberId);
      redirectAttributes.addFlashAttribute("success", "창고가 삭제되었습니다.");
    } catch (IllegalStateException e) {
      log.error("권한 오류", e);
      redirectAttributes.addFlashAttribute("error", "창고를 삭제할 권한이 없습니다.");
    } catch (Exception e) {
      log.error("창고 삭제 실패", e);
      redirectAttributes.addFlashAttribute("error", "창고 삭제에 실패했습니다: " + e.getMessage());
    }

    return "redirect:/warehouse/list";
  }
}