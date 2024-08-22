/*
package Trabook.PlanManager.repository;

import Trabook.PlanManager.domain.plan.Plan;
import Trabook.PlanManager.repository.plan.PlanMemoryRepository;
import Trabook.PlanManager.repository.plan.PlanRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class PlanMemoryRepositoryTest {

    PlanRepository planRepository = new PlanMemoryRepository();

    @AfterEach
    void afterEach() { planRepository.clearStore(); }

    @Test
    void save() {
        Plan plan1 = new Plan("asd","userA","asd","asd",true,0,0,"plan1");

        Plan savedPlan1 = planRepository.save(plan1);

        Assertions.assertThat(plan1).isEqualTo(savedPlan1);

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

    @Test
    void planDelete(){
        //given
        Plan plan1 = new Plan("asd","userA","asd","asd",true,0,0,"plan1");
        Plan plan2 = new Plan("bbb","userB","asd","asd",true,0,0,"plan1");

        planRepository.save(plan1);
        planRepository.save(plan2);
        //when
        planRepository.deletePlan(plan1);
        //then
        Assertions.assertThat(planRepository.findByUserAndName(plan2.getUserId(),plan2.getPlanName())).isTrue();
        Assertions.assertThat(planRepository.findByUserAndName(plan1.getUserId(), plan1.getPlanName())).isFalse();
    }

}
*/