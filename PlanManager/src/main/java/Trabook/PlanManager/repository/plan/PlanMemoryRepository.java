package Trabook.PlanManager.repository.plan;

/*
public class PlanMemoryRepository implements PlanRepository{

    private static final Map<String,List<Plan>> store = new ConcurrentHashMap<>(); //그냥 hashmap쓰면 안됨
    private static long sequence = 0L; //이거 멀티스레드 동시 접근 때문에 long말고 다른 타입으로 바꾸기(atomic)

    @Override
    public Plan save(Plan plan, List<Schedule> scheduleList) {
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
    public Plan findById(String planId) {
        return null;
    }

    @Override
    public boolean findByUserAndName(String userId, String planName) {   //return type Plan 객체로
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
    public boolean deletePlan(Plan plan) {  //plan객체 통째로 들고와서 삭제
        List<Plan> userPlanList = store.get(plan.getUserId());

        if(userPlanList!=null)
            return userPlanList.remove(plan);
        return false;
    }

    @Override
    public List<Plan> findPlanList() {  //계획 목록인데 몇개씩?
        return List.of();
    }

    @Override
    public int likePlan(String userId,String planId) {
        return 0;
    }

    @Override
    public int scrapPlan(String userId, String planId) {
        return 0;
    }

    @Override
    public List<Plan> findUserPlanList(String userId) {
        return store.get(userId);
    }

    @Override
    public List<Plan> findUserLikePlanList(String userId) {
        return List.of();
    }

    @Override
    public List<Plan> findUserScrapPlanList(String userId) {
        return List.of();
    }

    public void clearStore() {
        store.clear();
    }

}
*/