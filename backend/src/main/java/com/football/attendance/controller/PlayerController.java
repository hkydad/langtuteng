package com.football.attendance.controller;

import com.football.attendance.entity.Player;
import com.football.attendance.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@CrossOrigin
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @GetMapping
    public List<Player> list() {
        return playerService.list();
    }

    @GetMapping("/{id}")
    public Player getById(@PathVariable Long id) {
        return playerService.getById(id);
    }

    @PostMapping
    public Player save(@RequestBody Player player) {
        playerService.save(player);
        return player;
    }

    @PutMapping("/{id}")
    public Player update(@PathVariable Long id, @RequestBody Player player) {
        player.setId(id);
        playerService.update(player);
        return player;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        playerService.delete(id);
    }
}
