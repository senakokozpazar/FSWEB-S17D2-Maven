package com.workintech.s17d2.rest;

import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import com.workintech.s17d2.model.Developer;
import com.workintech.s17d2.model.Experience;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.workintech.s17d2.tax.DeveloperTax;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/developers")
public class DeveloperController {
  public Map<Integer, Developer> developers;
  private final Taxable taxable;

  @PostConstruct
    public void init(){
      developers = new HashMap<>();
  }

  @Autowired
  public DeveloperController(Taxable taxable) {
    this.taxable = taxable;
  }

  @GetMapping
    public List<Developer> getAllDevelopers(){
      return developers.values().stream().collect(Collectors.toList());
  }

  @GetMapping("/{id}")
    public Developer getDeveloperById(@PathVariable("id") int id){
      return developers.get(id);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
    public  void addDeveloper(@RequestBody Developer developer) {
      double finalSalary;

      if (developer.getExperience().equals(Experience.JUNIOR)) {
          finalSalary = developer.getSalary() - developer.getSalary() * taxable.getSimpleTaxRate();
      } else if (developer.getExperience().equals(Experience.MID)) {
          finalSalary = developer.getSalary() - developer.getSalary() * taxable.getMiddleTaxRate();
      } else if (developer.getExperience().equals(Experience.SENIOR)) {
          finalSalary = developer.getSalary() - developer.getSalary() * taxable.getUpperTaxRate();
      } else {
          throw new IllegalArgumentException("Invalid experience level.");
      }
      developer.setSalary(finalSalary);
      developers.put(developer.getId(), developer);
  }

  @PutMapping("/{id}")
    public Developer update(@PathVariable int id, @RequestBody Developer developer){
      developers.put(id, new Developer(id, developer.getName(), developer.getSalary(), developer.getExperience()));
      return developers.get(id);
  }

  @DeleteMapping("/{id}")
    public Developer delete(@PathVariable int id){
      Developer developer = developers.get(id);
      developers.remove(developer);
      return developer;
  }

}
