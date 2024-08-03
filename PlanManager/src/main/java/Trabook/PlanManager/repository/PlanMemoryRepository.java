package Trabook.PlanManager.repository;

import Trabook.PlanManager.domain.plan.Plan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PlanMemoryRepository implements PlanRepository{

    private static final Map<String,List<Plan>> store = new ConcurrentHashMap<>(); //그냥 hashmap쓰면 안됨
    private static long sequence = 0L; //이거 멀티스레드 동시 접근 때문에 long말고 다른 타입으로 바꾸기(atomic)

    @Override
    public Plan save(Plan plan) {
        String userId = plan.getUserId();
        plan.setPlanId((sequence++)+"");

        if(!store.containsKey(userId)){
            store.computeIfAbsent(userId, k -> new ArrayList<>()).add(plan);
        }else{
            store.get(userId).add(plan);
        }
        return plan;
    }

    @Override
    public boolean findByUserAndName(String userId, String planName) {
        List<Plan> userPlanList = store.get(userId);
        if(userPlanList!=null && !userPlanList.isEmpty()) {
            for (Plan plan : userPlanList) {
                if (plan.getPlanName().equals(planName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<Plan> findPlanList() {  //계획 목록인데 몇개씩?
        return List.of();
    }

    @Override
    public List<Plan> findUserPlanList() {
        return List.of();
    }

    public void clearStore() {
        store.clear();
    }

}
