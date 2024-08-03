package Trabook.PlanManager.repository;

import Trabook.PlanManager.domain.plan.Plan;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class PlanMemoryRepositoryTest {

    PlanRepository planRepository = new PlanMemoryRepository();

    //@AfterEach
    //void afterEach() { planRepository.clearStore(); }

    @Test
    void save() {
    }

    @Test
    void findByUserandName() {
        //given userA - plan1   userB - plan2 is saved
        Plan plan1 = new Plan("asd","userA","asd","asd",true,0,0,"plan1");
        Plan plan2 = new Plan("bbb","userB","asd","asd",true,0,0,"plan2");
        planRepository.save(plan1);
        planRepository.save(plan2);
        //when
        boolean checkFind1 = planRepository.findByUserAndName("userA","plan1");
        boolean checkFind2 = planRepository.findByUserAndName("userB","plan2");
        boolean checkNotFind1 = planRepository.findByUserAndName("userA","plan4");



        //then
        Assertions.assertThat(checkFind1).isTrue();
        Assertions.assertThat(checkFind2).isTrue();
        Assertions.assertThat(checkNotFind1).isFalse();

    }



}