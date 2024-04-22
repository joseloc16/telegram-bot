package org.joseloc.telegrambot.repository;

import org.joseloc.telegrambot.entity.WordDetect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBotRepository extends JpaRepository<WordDetect, Integer> {

}
