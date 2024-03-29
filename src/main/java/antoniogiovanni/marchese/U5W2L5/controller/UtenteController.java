package antoniogiovanni.marchese.U5W2L5.controller;

import antoniogiovanni.marchese.U5W2L5.exceptions.BadRequestException;
import antoniogiovanni.marchese.U5W2L5.model.Utente;
import antoniogiovanni.marchese.U5W2L5.payloads.NewUtenteDTO;
import antoniogiovanni.marchese.U5W2L5.payloads.ResponseDTO;
import antoniogiovanni.marchese.U5W2L5.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UtenteController {
    @Autowired
    private UtenteService usersService;

    @GetMapping
    public Page<Utente> getUsers(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 @RequestParam(defaultValue = "id") String orderBy) {
        return usersService.getUsers(page, size, orderBy);
    }

    @GetMapping("/{userId}")
    public Utente getUserById(@PathVariable UUID userId) {
        return usersService.findById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO createUser(@RequestBody @Validated NewUtenteDTO newUserPayload, BindingResult validation) {

        if (validation.hasErrors()) {
            throw new BadRequestException(validation.getAllErrors().stream().map(err -> err.getDefaultMessage()).toList().toString());
        } else {
            Utente newUser = usersService.save(newUserPayload);

            return new ResponseDTO(newUser.getId());
        }
    }

    @PutMapping("/{userId}")
    public Utente getUserByIdAndUpdate(@PathVariable UUID userId, @RequestBody NewUtenteDTO newUtenteDTO) {
        return usersService.findByIdAndUpdate(userId, newUtenteDTO);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void getUserByIdAndDelete(@PathVariable UUID userId) {
        usersService.findByIdAndDelete(userId);
    }

    @PostMapping("/add_device/{userId}/{deviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addDeviceToUser(@PathVariable UUID userId,@PathVariable UUID deviceId){
        usersService.addDeviceToUser(userId,deviceId);
    }

    @PatchMapping("/{userId}/avatar")
    public Utente uploadAvatar(@RequestParam("avatar") MultipartFile file, @PathVariable UUID userId) {
        try {
            return usersService.uploadAvatar(userId, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
