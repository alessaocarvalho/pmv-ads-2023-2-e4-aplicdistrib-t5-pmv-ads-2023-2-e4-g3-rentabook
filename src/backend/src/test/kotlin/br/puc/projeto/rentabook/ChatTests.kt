package br.puc.projeto.rentabook

import br.puc.projeto.rentabook.adapters.LocalDateAdapter
import br.puc.projeto.rentabook.adapters.LocalDateTimeAdapter
import br.puc.projeto.rentabook.dto.*
import br.puc.projeto.rentabook.model.ChatMessage
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import jakarta.validation.constraints.AssertTrue
import org.json.JSONObject
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@SpringBootTest(properties = ["spring.data.mongodb.database=rentabook_db_test"])
class ChatTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    /**
     * Inicia os usuário de teste e pega suas respectivar credenciais e ids.
     */

    @BeforeEach
    fun initializeDatabase() {
        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/register")
                .contentType("application/json")
                .content(
                    ObjectMapper().writeValueAsString(
                        UserForm(
                            name = "John",
                            email = "john@email.com",
                            password = "123456",
                        )
                    )
                )
            )
            .andExpect {
                val response = JSONObject(it.response.getContentAsString(StandardCharsets.UTF_8))
                userOneToken = response.getString("token")
            }

        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/user")
                .contentType("application/json")
                .header("Authorization", "Bearer $userOneToken")
            )
            .andExpect {
                val response = JSONObject(it.response.getContentAsString(StandardCharsets.UTF_8))
                userOneId = response.getString("id")
            }

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/register")
                .contentType("application/json")
                .content(
                    ObjectMapper().writeValueAsString(
                        UserForm(
                            name = "Mary",
                            email = "mary@email.com",
                            password = "123456",
                        )
                    )
                )
            )
            .andExpect {
                val response = JSONObject(it.response.getContentAsString(StandardCharsets.UTF_8))
                userTwoToken = response.getString("token")
            }

        mockMvc.perform(
            MockMvcRequestBuilders
                .get("/user")
                .contentType("application/json")
                .header("Authorization", "Bearer $userTwoToken")
            )
            .andExpect {
                val response = JSONObject(it.response.getContentAsString(StandardCharsets.UTF_8))
                userTwoId = response.getString("id")
            }

    }

    @BeforeEach
    fun clearDocument() {
        mongoTemplate.db.drop()
    }

    /**
     * Teste:       T-008
     * Requisito:   RF-008
     * Objetivo:    Tentar criar um chat entre dois usuários.
     */
    @Test
    fun `T008 - Criar chat`() {
        var addressId = ""
        var announcementId = ""

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/user/address")
                .contentType("application/json")
                .header("Authorization", "Bearer $userOneToken")
                .content(
                    ObjectMapper().writeValueAsString(
                        AddressForm(
                            name = "Casa",
                            cep = "01001-000",
                            street = "Praça da Sé",
                            number = "10",
                            complement = "casa",
                            neighborhood = "Sé",
                            city = "São Paulo",
                            state = "SP",
                        )
                    )
                )
            )
            .andExpect {
                val response = JSONObject(it.response.getContentAsString(StandardCharsets.UTF_8))
                addressId = response.getString("id")
            }

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/announcement/new")
                .contentType("application/json")
                .header("Authorization", "Bearer $userOneToken")
                .content(
                    ObjectMapper().writeValueAsString(
                        CreateAnnouncementForm(
                            bookId = "f1u-swEACAAJ",
                            images = listOf(),
                            description = "description",
                            dailyValue = 10,
                            locationId = addressId,
                        )
                    )
                )
            )
            .andExpect {
                val builder = GsonBuilder()
                builder.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
                val gson = builder.create()

                val announcementView = gson.fromJson(
                    it.response.getContentAsString(StandardCharsets.UTF_8),
                    AnnouncementView::class.java,
                )

                announcementId = announcementView.id

                Assertions.assertEquals("f1u-swEACAAJ", announcementView.book.id)
                Assertions.assertEquals("description", announcementView.description)
                Assertions.assertEquals(0, announcementView.images.size)
                Assertions.assertEquals(10, announcementView.dailyValue)
            }

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/announcement/rent")
                .contentType("application/json")
                .header("Authorization", "Bearer $userTwoToken")
                .content(
                    ObjectMapper().writeValueAsString(
                        CreateRentForm(
                            announcementId = announcementId,
                            startDate = "2023-09-23",
                            endDate = "2023-09-26",
                            value = 15.0,
                        )
                    )
                )
            )
            .andExpect(status().isOk)
            .andExpect {
                val builder = GsonBuilder()
                builder.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
                builder.registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
                val gson = builder.create()

                val rentView = gson.fromJson(
                    it.response.getContentAsString(StandardCharsets.UTF_8),
                    RentView::class.java
                )

                Assertions.assertNotNull(rentView.chat)
                Assertions.assertEquals(userOneId, rentView.chat.owner.id)
                Assertions.assertEquals(userTwoId, rentView.chat.lead.id)
            }
    }

    /**
     * Teste:       T-009
     * Requisito:   RF-008
     * Objetivo:    Tentar criar uma mensagem de chat entre dois usuários.
     */
    @Test
    fun `T008 - Criar mensagem de chat entre dois usuarios`() {
        var addressId = ""
        var announcementId = ""
        var chatId = ""

        val message = "Hello world!"

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/user/address")
                .contentType("application/json")
                .header("Authorization", "Bearer $userOneToken")
                .content(
                    ObjectMapper().writeValueAsString(
                        AddressForm(
                            name = "Casa",
                            cep = "01001-000",
                            street = "Praça da Sé",
                            number = "10",
                            complement = "casa",
                            neighborhood = "Sé",
                            city = "São Paulo",
                            state = "SP",
                        )
                    )
                )
            )
            .andExpect {
                val response = JSONObject(it.response.getContentAsString(StandardCharsets.UTF_8))
                addressId = response.getString("id")
            }

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/announcement/new")
                .contentType("application/json")
                .header("Authorization", "Bearer $userOneToken")
                .content(
                    ObjectMapper().writeValueAsString(
                        CreateAnnouncementForm(
                            bookId = "f1u-swEACAAJ",
                            images = listOf(),
                            description = "description",
                            dailyValue = 10,
                            locationId = addressId,
                        )
                    )
                )
            )
            .andExpect {
                val builder = GsonBuilder()
                builder.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
                val gson = builder.create()

                val announcementView = gson.fromJson(
                    it.response.getContentAsString(StandardCharsets.UTF_8),
                    AnnouncementView::class.java,
                )

                announcementId = announcementView.id

                Assertions.assertEquals("f1u-swEACAAJ", announcementView.book.id)
                Assertions.assertEquals("description", announcementView.description)
                Assertions.assertEquals(0, announcementView.images.size)
                Assertions.assertEquals(10, announcementView.dailyValue)
            }

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/announcement/rent")
                .contentType("application/json")
                .header("Authorization", "Bearer $userTwoToken")
                .content(
                    ObjectMapper().writeValueAsString(
                        CreateRentForm(
                            announcementId = announcementId,
                            startDate = "2023-09-23",
                            endDate = "2023-09-26",
                            value = 15.0,
                        )
                    )
                )
            )
            .andExpect(status().isOk)
            .andExpect {
                val builder = GsonBuilder()
                builder.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
                builder.registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
                val gson = builder.create()

                val rentView = gson.fromJson(
                    it.response.getContentAsString(StandardCharsets.UTF_8),
                    RentView::class.java
                )

                Assertions.assertNotNull(rentView.chat)
                Assertions.assertEquals(userOneId, rentView.chat.owner.id)
                Assertions.assertEquals(userTwoId, rentView.chat.lead.id)

                chatId = rentView.chat.id
            }

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/chat_messages/new")
                .contentType("application/json")
                .header("Authorization", "Bearer $userOneToken")
                .content(
                    ObjectMapper().writeValueAsString(
                        CreateChatMessageForm(
                            chatId = chatId,
                            message = message,
                        )
                    )
                )
            )
            .andExpect(status().isOk())
            .andExpect {
                val chatMessageView = Gson().fromJson(
                    it.response.getContentAsString(StandardCharsets.UTF_8),
                    ChatMessageView::class.java,
                )
                Assertions.assertEquals(message, chatMessageView.message)
                Assertions.assertEquals(userOneId, chatMessageView.sender.id)
            }
    }

    /**
     * Limpa base de dados apos os testes.
     */

    companion object {
        private var userOneToken = ""
        private var userTwoToken = ""

        private var userOneId = ""
        private var userTwoId = ""

        @JvmStatic
        @AfterAll
        fun clearDatabase(@Autowired mongoTemplate: MongoTemplate) {
            mongoTemplate.db.drop()
        }
    }
}