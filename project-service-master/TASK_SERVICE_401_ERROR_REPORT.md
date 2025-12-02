# Task Service 401 Unauthorized Hatası - Detaylı Rapor

## Tarih: 2025-12-01

## Sorun Özeti

`/api/v1/project/read/all/details` endpoint'ine istek atıldığında **500 Internal Server Error** dönüyor.  
**Kök Neden:** Task service'te **401 Unauthorized** hatası oluşuyor.

---

## Hata Akışı

### 1. İstek Akışı
```
Postman/Client
    ↓
Project Service (8082)
    ↓ GET /api/v1/project/read/all/details
ProjectController.getProjectsWithDetails()
    ↓
ProjectServiceImpl.readAllProjectsWithDetails()
    ↓
ProjectServiceImpl.retrieveProjectDetails() (her proje için)
    ↓
TaskClient.getCountsByProject(projectCode) [Feign Client]
    ↓
Task Service (8383)
    ↓ GET /api/v1/task/count/project/{projectCode}
TaskController veya TaskServiceImpl
    ↓ (user-service'e istek atılıyor)
UserClient.getUserByUsername() [Feign Client]
    ↓
User Service
    ❌ 401 Unauthorized
```

### 2. Hata Mesajları

**Task Service Logları:**
```
[ERROR] javax.ws.rs.NotAuthorizedException: HTTP 401 Unauthorized
```

**Project Service Logları:**
```
[ERROR] [500] during [GET] to [http://task-service/api/v1/task/count/project/SP001] 
[TaskClient#getCountsByProject(String)]: 
{"success":false,"message":"Action failed: An error occurred!","httpStatus":"INTERNAL_SERVER_ERROR"}
```

---

## Tespit Edilen Sorunlar

### ✅ Çözülen Sorunlar

1. **Project Service - Bug Fix**
   - **Dosya:** `ProjectServiceImpl.java` (satır 222)
   - **Sorun:** `return new ProjectDTO();` yerine `return projectDTO;` olmalıydı
   - **Durum:** ✅ Düzeltildi

2. **Task Service - Paket Adı Sorunu**
   - **Dosya:** `UserResponse.java`
   - **Sorun:** Paket adı ve dosya yolu uyumsuzluğu
   - **Durum:** ✅ Düzeltildi

### 🔴 Aktif Sorun: 401 Unauthorized

**Sorun:** Task service'ten user-service'e istek atılırken **401 Unauthorized** hatası alınıyor.

**Lokasyon:**
- Task service'te `getCountsByProject()` metodu içinde
- User-service'e istek atılırken

**Olası Nedenler:**

#### Senaryo 1: SecurityContext Boş
**Belirti:** Task service'te SecurityContext'te authentication yok  
**Açıklama:** 
- Task service'e gelen istekte SecurityContext var
- Ama task service içinde user-service'e istek atılırken SecurityContext boş olabilir
- FeignClientInterceptor SecurityContext'ten token alıyor, eğer boşsa token alınamaz

**Kontrol:**
```java
// Task service'te getCountsByProject() metodunda
SecurityContextHolder.getContext().getAuthentication() // null mu?
```

#### Senaryo 2: Token Formatı Yanlış
**Belirti:** Token formatı user-service tarafından kabul edilmiyor  
**Açıklama:**
- Task service'teki `getAccessToken()` metodu "Bearer " prefix'i ekliyor ✅
- Ama user-service token'ı kabul etmiyor olabilir

**Kontrol:**
- Token'ın doğru formatı: `Bearer <token>`
- User service'in token validation'ı çalışıyor mu?

#### Senaryo 3: Token Expire Olmuş
**Belirti:** Token süresi dolmuş  
**Açıklama:**
- Task service'e gelen istekteki token expire olmuş olabilir
- User-service bu token'ı kabul etmiyor

**Kontrol:**
- Token'ın expire time'ı kontrol edilmeli
- Yeni token ile test edilmeli

#### Senaryo 4: FeignClientInterceptor Çalışmıyor
**Belirti:** Interceptor token'ı header'a eklemiyor  
**Açıklama:**
- FeignClientInterceptor bean olarak register edilmemiş olabilir
- Ya da user-service'e istek atılırken interceptor devreye girmiyor

**Kontrol:**
- `@Component` annotation'ı var mı?
- Spring context'te bean olarak register edilmiş mi?

---

## İnceleme Gereken Kodlar

### Task Service Tarafında Kontrol Edilmesi Gerekenler:

#### 1. TaskServiceImpl.getCountsByProject()
```java
// Bu metod user-service'e nasıl istek atıyor?
// UserClient kullanılıyor mu?
// SecurityContext korunuyor mu?
```

#### 2. UserClient (Task Service'te)
```java
@FeignClient(name = "user-service")
public interface UserClient {
    // Hangi endpoint'e istek atılıyor?
    // Token gönderiliyor mu?
}
```

#### 3. FeignClientInterceptor (Task Service'te)
```java
@Component
public class FeignClientInterceptor implements RequestInterceptor {
    // getAccessToken() doğru çalışıyor mu?
    // SecurityContext'te authentication var mı?
}
```

#### 4. KeycloakServiceImpl.getAccessToken() (Task Service'te)
```java
// Bu metod doğru token döndürüyor mu?
// SecurityContext'ten token alınabiliyor mu?
```

---

## Çözüm Önerileri

### Çözüm 1: SecurityContext Kontrolü
Task service'te user-service'e istek atılmadan önce SecurityContext kontrol edilmeli:

```java
// TaskServiceImpl.getCountsByProject() içinde
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
if (auth == null) {
    // Token'ı başka bir şekilde al veya hata fırlat
}
```

### Çözüm 2: Token'ı Manuel Gönderme
Eğer SecurityContext boşsa, token'ı başka bir şekilde al:

```java
// Örneğin: Request header'dan token'ı al
HttpServletRequest request = ((ServletRequestAttributes) 
    RequestContextHolder.currentRequestAttributes()).getRequest();
String token = request.getHeader("Authorization");
```

### Çözüm 3: Async Context Propagation
Eğer async işlem yapılıyorsa, SecurityContext propagate edilmeli:

```java
SecurityContext context = SecurityContextHolder.getContext();
// Async işlemde context'i koru
```

### Çözüm 4: FeignClientInterceptor Debug
Interceptor'ın çalışıp çalışmadığını kontrol et:

```java
@Override
public void apply(RequestTemplate requestTemplate) {
    String token = keycloakService.getAccessToken();
    // Log ekle
    System.out.println("Token: " + token);
    requestTemplate.header("Authorization", token);
}
```

---

## Test Adımları

### 1. Task Service'te SecurityContext Kontrolü
```java
// TaskServiceImpl.getCountsByProject() başında
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
System.out.println("Auth: " + auth);
System.out.println("Auth Type: " + (auth != null ? auth.getClass().getName() : "NULL"));
```

### 2. FeignClientInterceptor Debug
```java
@Override
public void apply(RequestTemplate requestTemplate) {
    try {
        String token = keycloakService.getAccessToken();
        log.info("FeignClientInterceptor - Token: {}", token != null ? "EXISTS" : "NULL");
        requestTemplate.header("Authorization", token);
    } catch (Exception e) {
        log.error("FeignClientInterceptor - Error: ", e);
    }
}
```

### 3. User Service'e Doğrudan İstek
Task service'ten user-service'e doğrudan curl ile istek at:
```bash
curl -X GET "http://localhost:8081/api/v1/user/read/{username}" \
  -H "Authorization: Bearer {TOKEN}"
```

---

## Beklenen Sonuç

✅ Task service user-service'e başarıyla istek atabilmeli  
✅ 401 hatası çözülmeli  
✅ `getCountsByProject()` metodu başarıyla çalışmalı  
✅ Project service'ten gelen istek başarıyla tamamlanmalı

---

## Notlar

- **Mantık değişikliği yapılmadı**, sadece bug fix'ler yapıldı
- Task service'teki sorun **authentication/authorization** ile ilgili
- SecurityContext propagation sorunu olabilir
- FeignClientInterceptor'ın çalışıp çalışmadığı kontrol edilmeli

---

**Durum:** 🔴 Aktif - Task service'te 401 hatası devam ediyor  
**Öncelik:** Yüksek - Bu sorun çözülmeden endpoint çalışmıyor


