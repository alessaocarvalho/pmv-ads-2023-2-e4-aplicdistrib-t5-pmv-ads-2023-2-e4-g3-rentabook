package br.puc.projeto.rentabook.services

import br.puc.projeto.rentabook.forms.UserForm
import br.puc.projeto.rentabook.models.User
import br.puc.projeto.rentabook.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.Exception
import java.time.LocalDateTime

@Service
class UserService {

    @Autowired
    private lateinit var userRepository: UserRepository

    fun registerUser(userForm: UserForm): User {
        if (userRepository.findByEmail(userForm.email) != null) {
            throw Exception("Este email já foi registrado!");
        }
        return userRepository.save(userForm.toUser())
    }

    fun getAll(): List<User> {
        return userRepository.findAll()
    }
}