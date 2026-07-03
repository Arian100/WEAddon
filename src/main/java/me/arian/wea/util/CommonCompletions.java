package me.arian.wea.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommonCompletions {

    public static List<String> players() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }

    public static List<String> numbersFromTo(int from, int to) {
        List<String> num = new ArrayList<>();
        for (int i = from; i < to; i++) {
            num.add(String.valueOf(i));
        }
        return num;
    }

    public static List<String> fromEnum(Class<? extends Enum<?>> enumClass, boolean toLowerCase) {
        if (enumClass.isEnum()) {
            if (toLowerCase) {
                List<String> e = new ArrayList<>();
                for (Enum<?> enu : enumClass.getEnumConstants()) {
                    e.add(enu.name().toLowerCase());
                }
                return e;
            } else {
                return Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toList();
            }
        } else {
            return Collections.emptyList();
        }
    }
}
