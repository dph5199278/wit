package com.cs.wit.persistence.es;

import com.cs.wit.socketio.message.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.transaction.annotation.Transactional;

public interface ChatMessageEsRepository extends ElasticsearchRepository<ChatMessage,String> {

    Page<ChatMessage> findByUsessionAndMessageContaining(String usession, String message, Pageable page);

}
