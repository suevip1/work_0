package com.dtstack.engine.master.enums;

/**
 * @Auther: dazhi
 * @Date: 2023-07-05 11:51
 * @Email: dazhi@dtstack.com
 * @Description: PriorityEnum
 */
public enum PriorityEnum {

    one(1),two(2),three(3),four(4),five(5);

    private final Integer number;

    PriorityEnum(Integer number) {
        this.number = number;
    }

    public Integer getNumber() {
        return number;
    }
}
