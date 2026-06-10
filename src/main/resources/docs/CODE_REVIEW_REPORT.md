## Issue #1: [Название проблемы]

**Категория:** API Design / Security / Error Handling / Code Quality
**Приоритет:** CRITICAL / MAJOR / MINOR
**Местоположение:** InviteeController.java, строка [X], метод [название]

**Что плохо:**
[Конкретный фрагмент кода]

**Почему плохо:**
[Нарушение какого принципа/стандарта, какой риск]

**Как исправить:**
[Конкретный пример кода или ссылка на документацию]

## Issue #1: Плохой naming: глаголы в URL

**Категория:** API Design
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 24, метод getInvitees

**Что плохо:**
@PostMapping("/getInvitees")

**Почему плохо:**
Нарушение Restful-принципа 

**Как исправить:**
@GetMapping("/invitees")

## Issue #3: Entity вместо DTO в response

**Категория:** API Design
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 32, метод getById

**Что плохо:**
return repository.findById(id).orElse(null);

**Почему плохо:**
Нарушение Restfull-принципов

**Как исправить:**
public ResponseEntity<InviteeResponse> getById(@PathVariable UUID id) {
Invitee invitee = service.getById(id);
return ResponseEntity.ok(mapper.toResponse(invitee)); 

## Issue #4: SQL injection через конкатенацию

**Категория:** Security 
**Приоритет:** CRITICAL 
**Местоположение:** InviteeController.java, строка 42, метод create

**Что плохо:**
String sql = "SELECT COUNT(*) FROM invitees WHERE email = '" + email + "'";

**Почему плохо:**
Нарушение Security-принципов

**Как исправить:**
Invitee findByEmail(String email); // Автоматическое экранирование

// Или PreparedStatement
PreparedStatement ps = conn.prepareStatement("SELECT * FROM invitees WHERE email = ?");
ps.setString(1, email);

## Issue #5: Exposure внутренних полей

**Категория:** Security 
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 51, метод create

**Что плохо:**
Invitee invitee = new Invitee();
invitee.setId(UUID.randomUUID());
invitee.setEmail(email);
invitee.setFirstName(firstName);
invitee.setCreatedAt(Instant.now());

return repository.save(invitee);

**Почему плохо:**
Нарушение Security-принципов

**Как исправить:**
Invitee invitee = userService.getById(id);
return ResponseEntity.ok(userMapper.toResponse(invitee)); // password не попадёт в JSON
}

## Issue #6: @RequestBody без @Valid

**Категория:** Security
**Приоритет:** CRITICAL 
**Местоположение:** InviteeController.java, строка 66, метод updateStatus

**Что плохо:**
public Invitee updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body)

**Почему плохо:**
Нет валидации входных данных

**Как исправить:**
public ResponseEntity<InviteeResponse>  create(@Valid @RequestBody CreateInviteeRequest request)

## Issue #7: Пустые catch блоки

**Категория:** Security
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 79, метод updateStatus

**Что плохо:**
} catch (Exception e) {
// Пустой catch
return null;
}
**Почему плохо:**
Клиент получит null вместо error response

**Как исправить:**
public ResponseEntity<InviteeResponse> updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body)
Invitee invitee = service.getById(id); // Service выбросит EntityNotFoundException
return ResponseEntity.ok(mapper.toResponse(invitee));

## Issue #8: 500 на бизнес-ошибки вместо 4xx

**Категория:** Security
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 71, метод updateStatus

**Что плохо:**
// Бизнес-логика в контроллере
if (status.equals("ACTIVE") || status.equals("INACTIVE")) {
invitee.setStatus(status);
} else {
throw new RuntimeException("Invalid status");
}

**Почему плохо:**
Бизнес-exceptions (`EmailAlreadyExistsException`) возвращают `500`

**Как исправить:**
// Custom exception
public class EmailAlreadyExistsException extends RuntimeException {
public EmailAlreadyExistsException(String status) {
super("Status already exists: " + status);
}
}

// В Service
if (repository.existsByEmail(request.status())) {
throw new StatusAlreadyExistsException(request.status());
}

// GlobalExceptionHandler
@ExceptionHandler(StatusAlreadyExistsException.class)
public ResponseEntity<ProblemDetail> handleStatysExists(StatusAlreadyExistsException ex) {
ProblemDetail problem = ProblemDetail.forStatusAndDetail(
HttpStatus.CONFLICT, // 409 Conflict (бизнес-ошибка, не server error)
ex.getMessage()
);
return ResponseEntity.status(409).body(problem);
}

## Issue #8: Бизнес-логика в контроллере

**Категория:** Security
**Приоритет:** MAJOR
**Местоположение:** InviteeController.java, строка 72, метод updateStatus

**Что плохо:**
// Бизнес-логика в контроллере
if (status.equals("ACTIVE") || status.equals("INACTIVE")) {
invitee.setStatus(status);
} else {
throw new RuntimeException("Invalid status");
}

**Почему плохо:**
Нарушение Single Responsibility Principle

**Как исправить:**
@PutMapping("/invitees/{id}/status")
public ResponseEntity<InviteeResponse> updateStatus(
@PathVariable UUID id,
@Valid @RequestBody UpdateInviteeStatusRequest request) {

InviteeResponse updated = inviteeService.updateStatus(id, request.status());
return ResponseEntity.ok(updated);
    }
} 

## Issue #9: Неправильный статус

**Категория:** API Design 
**Приоритет:** CRITICAL 
**Местоположение:** InviteeController.java, строка 56, метод delete

**Что плохо:**
Invitee invitee = repository.findById(id).orElse(null);

**Почему плохо:**
Нарушения RestFull стиля

**Как исправить:**
Invitee invitee = repository.findById(id)
.orElseThrow(() -> new EntityNotFoundException("Invitee not found"));

## Issue #10: Возвращает Entity вместо DTO 

**Категория:** API Design
**Приоритет:** CRITICAL
**Местоположение:** InviteeController.java, строка 61, метод delete

**Что плохо:**
return invitee;

**Почему плохо:**
Нарушения RestFull принципов

**Как исправить:**
return ResponseEntity.noContent().build();
