package Trabook.PlanManager.repository;

import Trabook.PlanManager.domain.plan.Plan;
import jakarta.persistence.EntityManager;

import java.util.List;

public class JpaPlanRepository implements PlanRepository{
    private final EntityManager em;

    public JpaPlanRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public Plan save(Plan plan) {
        return plan;
    }

    @Override
    public boolean findByUserAndName(String userName, String planName) {
        return false;
    }

    @Override
    public boolean deletePlan(Plan plan) {
        return false;
    }

    @Override
    public List<Plan> findPlanList() {
        return List.of();
    }

    @Override
    public List<Plan> findUserPlanList() {
        return List.of();
    }

    @Override
    public List<Plan> findUserPlanList(String userId) {
        return List.of();
    }

    @Override
    public int likePlan(String planId) {
        return 0;
    }

    @Override
    public void clearStore() {

    }
}
