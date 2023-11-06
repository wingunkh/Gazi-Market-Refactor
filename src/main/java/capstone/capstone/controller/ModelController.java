package capstone.capstone.controller;

import capstone.capstone.domain.Model;
import capstone.capstone.service.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ModelController {
    @Autowired
    private ModelService modelService;

    // 모델 저장
    @PostMapping("/model")
    public Model createModel(@RequestBody Model model) {
        System.out.println(model.getModelName() + "모델 추가");
        return modelService.createModel(model);
    }

    // 전체 모델 목록 리턴
    @GetMapping("/model")
    public List<Model> getAllModel() {
        System.out.println("전체 모델 목록 반환");
        return modelService.getAllModel();
    }

    // 해당 카테고리의 모델 목록 리턴
    @GetMapping("/{category_name}/model")
    public List<Model> getModelName(@PathVariable String category_name){
        System.out.println(category_name + " 카테고리 " + "모델 목록 반환");
        return modelService.getModelName(category_name);
    }

    // 해당 모델의 해당 등급의 최근 거래가 리턴
    @GetMapping("/{model}/{grade}")
    public double getMarketPrice(@PathVariable String model_name, @PathVariable String grade){
        System.out.println(model_name + "의 " + grade + "등급 최근 거래가 반환");
        return modelService.getMarketPrice(model_name, grade);
    }
}