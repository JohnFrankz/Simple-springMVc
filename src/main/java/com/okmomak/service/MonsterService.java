package com.okmomak.service;

import com.okmomak.entity.Monster;

import java.util.List;

public interface MonsterService {
    List<Monster> listMonster();

    List<Monster> findMonsterByName(String name);

    List<Monster> findMonsterBySkill(String skill);
}
