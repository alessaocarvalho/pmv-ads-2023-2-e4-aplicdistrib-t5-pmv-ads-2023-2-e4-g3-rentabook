package br.puc.projeto.rentabook.service

import br.puc.projeto.rentabook.dto.ChatView
import br.puc.projeto.rentabook.dto.CreateChatForm
import br.puc.projeto.rentabook.mapper.ChatViewMapper
import br.puc.projeto.rentabook.mapper.CreateChatFormMapper
import br.puc.projeto.rentabook.repository.ChatRepository
import br.puc.projeto.rentabook.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val createChatFormMapper: CreateChatFormMapper,
    private val chatViewMapper: ChatViewMapper,
) {
    fun createChat(createChatForm: CreateChatForm): ChatView {
        val authentication = SecurityContextHolder.getContext().authentication
        return userRepository.findByEmail(authentication.name).run {
            this ?: throw Exception("Usuário autenticado não encontrado")
            chatRepository.save(createChatFormMapper.map(createChatForm)).run {
                chatViewMapper.map(this)
            }
        }
    }
}