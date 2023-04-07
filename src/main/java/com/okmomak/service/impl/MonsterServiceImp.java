package com.okmomak.service.impl;

import com.okmomak.controller.a.A;
import com.okmomak.entity.Monster;
import com.okmomak.service.MonsterService;
import com.okmomak.spring.annotation.Autowired;
import com.okmomak.spring.annotation.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MonsterServiceImp implements MonsterService {

    // @Autowired
    // private A a;
    @Override
    public List<Monster> listMonster() {
        List<Monster> monsters = new ArrayList<>();
        monsters.add(new Monster(1, "John", 2, "吹风"));
        monsters.add(new Monster(2, "frank", 22, "笑"));
        return monsters;
    }

    @Override
    public List<Monster> findMonsterByName(String name) {
        List<Monster> monsters = new ArrayList<>();
        monsters.add(new Monster(1, "John", 2, "吹风"));
        monsters.add(new Monster(2, "frank", 22, "笑"));
        monsters.add(new Monster(1, "John", 2, "吹风1"));
        monsters.add(new Monster(2, "frank", 22, "笑1"));
        monsters.add(new Monster(1, "John", 2, "吹风2"));
        monsters.add(new Monster(2, "frank", 22, "笑2"));

        List<Monster> res = new ArrayList<>();
        for (Monster monster : monsters) {
            if (name.equals(monster.getName())) {
                res.add(monster);
            }
        }
        return res;
    }

    @Override
    public List<Monster> findMonsterBySkill(String skill) {
        List<Monster> monsters = new ArrayList<>();
        monsters.add(new Monster(1, "John", 2, "吹风"));
        monsters.add(new Monster(2, "frank", 22, "笑"));
        monsters.add(new Monster(1, "John", 2, "吹风1"));
        monsters.add(new Monster(2, "frank", 22, "笑1"));
        monsters.add(new Monster(1, "John", 2, "吹风2"));
        monsters.add(new Monster(2, "frank", 22, "hh"));

        List<Monster> res = new ArrayList<>();
        for (Monster monster : monsters) {
            if (skill.equals(monster.getSkill())) {
                res.add(monster);
            }
        }
        return res;
    }
}
