import method.Equipment;
import method.EquipmentStore;
import method.OrderCounter;

import java.util.*;


void main() {
//    D1 d =new D1();
//    System.out.println(d.handleList(List.of("ACTIVE","ACTIVE","ARCHIVED","RETIRED")));
//    System.out.println(d.handleList(List.of("ACTIVE")));
//    System.out.println(d.handleList(List.of()));
//    System.out.println(new D1().handleList(List.of()));


    Equipment E1 = new Equipment("EQ-A001", "SIM-P100", "ACTIVE");
    Equipment E2 = new Equipment("EQ-A002", "SIM-P100", "ACTIVE");
//    try {
//        E1.setCode(null);
//    }
//    catch (IllegalArgumentException ex)
//    {
//        System.out.println("传入空编号"+ex.getMessage());
//    }

//    OrderCounter c=new OrderCounter();
//    System.out.println(c.countPriority(List.of("HIGH","NORMAL","LOW","NORMAL","HIGH","HIGH") ));
//    System.out.println(c.countPriority(List.of("HIGH","UNKNOWN")));
//    System.out.println(c.countPriority(List.of()));

    EquipmentStore ES = new EquipmentStore();
//    ES.add(E1);
//    ES.add(E2);
//    var res = ES.findByCode("EQ-A001");
//    if (res.isPresent()) {
//        System.out.println("找到了 :" + res.get().getModel());
//    }else{
//        System.out.println("没有找到");
//    }
//    res= ES.findByCode("000");
//    if (res.isPresent()) {
//        System.out.println("找到了" + res.get());
//    }else{
//        System.out.println("没有找到");
//    }
    ES.addFromLine("EQ-A003,SIM-D200,ACTIVE");   // ① 正常
    var res = ES.findByCode("EQ-A003");
    System.out.println("找到了 :" + res.toString());
    ES.addFromLine("EQ-A004,SIM-P100");          // ② 少一个字段（坏数据）

}