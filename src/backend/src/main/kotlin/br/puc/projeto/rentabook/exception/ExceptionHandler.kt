package br.puc.projeto.rentabook.exception

import br.puc.projeto.rentabook.dto.ErrorView
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import kotlin.Exception

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExistsException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun alreadyExists(
        exception: ResourceAlreadyExistsException,
        request: HttpServletRequest
    ): ErrorView {
        return ErrorView(
            status = HttpStatus.CONFLICT.value(),
            error = HttpStatus.CONFLICT.name,
            message = exception.message,
            path = request.servletPath
        )
    }
    @ExceptionHandler(InvalidTokenException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun invalidToken(
        exception: InvalidTokenException,
        request: HttpServletRequest
    ): ErrorView {
        return ErrorView(
            status = HttpStatus.UNAUTHORIZED.value(),
            error = HttpStatus.UNAUTHORIZED.name,
            message = exception.message,
            path = request.servletPath
        )
    }
    @ExceptionHandler(ImageTypeNotSupportedException::class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    fun imageType(
        exception: ImageTypeNotSupportedException,
        request: HttpServletRequest
    ): ErrorView {
        return ErrorView(
            status = HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
            error = HttpStatus.UNSUPPORTED_MEDIA_TYPE.name,
            message = exception.message,
            path = request.servletPath
        )
    }
    @ExceptionHandler(InvalidLoginException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun invalidLogin(
        exception: InvalidLoginException,
        request: HttpServletRequest
    ): ErrorView {
        return ErrorView(
            status = HttpStatus.UNAUTHORIZED.value(),
            error = HttpStatus.UNAUTHORIZED.name,
            message = exception.message,
            path = request.servletPath
        )
    }
    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(
        exception: NotFoundException,
        request: HttpServletRequest
    ): ErrorView {
        return ErrorView(
            status = HttpStatus.NOT_FOUND.value(),
            error = HttpStatus.NOT_FOUND.name,
            message = exception.message,
            path = request.servletPath
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationError(
        exception: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ErrorView {
        val errorMessage = mutableMapOf<String, String?>()
        exception.bindingResult.fieldErrors.forEach { e ->
            errorMessage.put(e.field, e.defaultMessage)
        }
        return ErrorView(
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.name,
            message = errorMessage.toString(),
            path = request.servletPath
        )
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleServerError(
        exception: Exception,
        request: HttpServletRequest
    ): ErrorView {
        return ErrorView(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = HttpStatus.INTERNAL_SERVER_ERROR.name,
            message = exception.message,
            path = request.servletPath
        )
    }
}
