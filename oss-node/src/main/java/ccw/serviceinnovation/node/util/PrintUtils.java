package ccw.serviceinnovation.node.util;

import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PrintUtils {

    public static void log3D(){
        System.out.println("                                                                                                         ");
        System.out.println("                                                                                                         ");
        System.out.println("             ,--,                                                                                         ");
        System.out.println("           ,--.'|                                    ,---,                                                ");
        System.out.println("           |  | :       ,---.            ,--,      ,---.'|                                        ,---,  " + " Cloud Can 2.0");
        System.out.println("   ,---.   |  ' |     /   ,'\\         ,'_ /|      |   | :                                    ,-+-. /  | " + "  Running in Cluster mode");
        System.out.println("  /     \\  '  | |    .   ; ,. : ,'_ /| :  . |    ,--.__| |           ,---.      ,--.--.     ,--.'|'   | " + "  Port: "+ RegisterConstant.PORT);
        System.out.println(" /    / '  |  | :    '   | |: : |  ' | |  . .   /   ,'   |          /    / '   .--.  .-. |  |   |  ,\"' | "+ " Pid:  "+ ProcessUtil.getId());
        System.out.println(".    ' /   '  : |__  '   | .; : |  | ' |  | |  .   '  /  |         .    ' /     \\__\\/ : . .  |   | /  | | "+ "Group:" + RegisterConstant.GROUP_NAME);
        System.out.println("'   ; :__  |  | '.'| |   :    | :  | : ;  ; |  '   ; |:  |         '   ; :__    ,\" .--.; |  |   | |  | | " + " Nodes:" + RegisterConstant.GROUP_CLUSTER.split(",").length);
        System.out.println("'   | '.'| ;  :    ;  \\   \\  /  '  :  `--'   \\ |   | '/  '         '   | '.'|  /  /  ,.  |  |   | |  |/  " + " Shards:" + RegisterConstant.TOTAL_SHARDS);
        System.out.println("|   :    : |  ,   /    `----'   :  ,      .-./ |   :    :|         |   :    : ;  :   .'   \\ |   |/       " + " Disks:" + RegisterConstant.PARTITION_DISK.length);
        System.out.println(" \\   \\  /   ---`-'               `--`----'      \\   \\  /            \\   \\  /  |  ,     .-./ '---'        ");
        System.out.println("  `----'                                         `----'              `----'    `--`---'                  ");
        System.out.println("                                                                                                         ");
    }
}
