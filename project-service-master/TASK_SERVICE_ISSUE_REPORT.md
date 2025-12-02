# Task Service Sorun Raporu

## Tarih: 2025-12-01

## Sorun Özeti

`/api/v1/project/read/all/details` endpoint'ine istek atıldığında **500 Internal Server Error** dönüyor.

## Yapılan Düzeltmeler

### ✅ Project Service - Bug Fix
**Dosya:** `src/main/java/com/cydeo/service/impl/ProjectServiceImpl.java`  
**Satır:** 222  
**Sorun:** `retrieveProjectDetails()` metodunda `return new ProjectDTO();` yerine `return projectDTO;` olmalıydı.  
**Durum:** ✅ Düzeltildi

## Tespit Edilen Sorunlar

### 🔴 Ana Sorun: Task Service 500 Hatası

**Hata Mesajı:**
```
[500] during [GET] to [http://task-service/api/v1/task/count/project/SP001] 
[TaskClient#getCountsByProject(String)]: 
{"success":false,"message":"Action failed: An error occurred!","httpStatus":"INTERNAL_SERVER_ERROR"}
```

**Lokasyon:**
- **Dosya:** `src/main/java/com/cydeo/service/impl/ProjectServiceImpl.java`
- **Metod:** `retrieveProjectDetails(Project project)`
- **Satır:** 207

**Akış:**
1. `readAllProjectsWithDetails()` metodu çağrılıyor
2. Manager'ın projeleri bulunuyor
3. Her proje için `retrieveProjectDetails()` çağrılıyor
4. `taskClient.getCountsByProject(projectCode)` ile task-service'e istek atılıyor
5. Task service **500 hatası** dönüyor
6. `ProjectDetailsNotRetrievedException` fırlatılıyor

## Task Service İnceleme Gereken Noktalar

### 1. Endpoint Kontrolü
**Endpoint:** `GET /api/v1/task/count/project/{projectCode}`

**Kontrol Edilmesi Gerekenler:**
- ✅ Endpoint tanımlı mı?
- ✅ Doğru HTTP metodu kullanılıyor mu?
- ✅ Path variable doğru alınıyor mu?
- ✅ Response formatı doğru mu?

### 2. Task Service Logları
**Kontrol Edilmesi Gerekenler:**
- Task service loglarında ne hatası var?
- Exception stack trace'i nedir?
- Hangi satırda hata oluşuyor?

### 3. Feign Client Yapılandırması
**Project Service Tarafı:**
```java
@FeignClient(name = "task-service")
public interface TaskClient {
    @GetMapping("/api/v1/task/count/project/{projectCode}")
    ResponseEntity<TaskResponse> getCountsByProject(@PathVariable("projectCode") String projectCode);
}
```

**Kontrol Edilmesi Gerekenler:**
- Task service'te bu endpoint var mı?
- Service name doğru mu? (`task-service`)
- Eureka'da service kayıtlı mı?

### 4. Response Formatı
**Beklenen Response:**
```json
{
  "success": true,
  "statusCode": "OK",
  "message": "...",
  "data": {
    "completedTaskCount": 5,
    "uncompletedTaskCount": 3
  }
}
```

**Kontrol Edilmesi Gerekenler:**
- Task service bu formatta response dönüyor mu?
- `TaskResponse` DTO'su doğru mu?
- Data içindeki map yapısı doğru mu?

## Olası Sorun Senaryoları

### Senaryo 1: Task Service Endpoint Yok
**Belirti:** 404 Not Found veya 500 Internal Server Error  
**Çözüm:** Task service'te endpoint'i kontrol et ve ekle

### Senaryo 2: Task Service'te Exception
**Belirti:** 500 Internal Server Error  
**Çözüm:** Task service loglarını incele, exception'ı düzelt

### Senaryo 3: Service Discovery Sorunu
**Belirti:** Connection refused veya timeout  
**Çözüm:** Eureka'da task-service kayıtlı mı kontrol et

### Senaryo 4: Authentication/Authorization Sorunu
**Belirti:** 401/403 hatası  
**Çözüm:** FeignClientInterceptor token gönderiyor mu kontrol et

### Senaryo 5: Response Format Uyumsuzluğu
**Belirti:** Deserialization hatası  
**Çözüm:** TaskResponse DTO'sunu kontrol et

## Test Adımları

### 1. Task Service'i Doğrudan Test Et
```bash
curl -X GET "http://localhost:8083/api/v1/task/count/project/SP001" \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json"
```

### 2. Eureka'da Service Kontrolü
- Eureka dashboard'u aç: `http://localhost:8761`
- `task-service` kayıtlı mı kontrol et

### 3. Task Service Loglarını İncele
- Task service log dosyasını aç
- Exception stack trace'i bul
- Hatanın kaynağını tespit et

## Önerilen Düzeltmeler

### Task Service Tarafında Yapılması Gerekenler:

1. **Endpoint Kontrolü:**
   - `GET /api/v1/task/count/project/{projectCode}` endpoint'i var mı?
   - Doğru controller'da tanımlı mı?

2. **Exception Handling:**
   - GlobalExceptionHandler doğru çalışıyor mu?
   - Exception'lar yakalanıyor mu?

3. **Response Formatı:**
   - `TaskResponse` DTO'su doğru mu?
   - Data içinde `completedTaskCount` ve `uncompletedTaskCount` var mı?

4. **Service Discovery:**
   - Task service Eureka'ya kayıtlı mı?
   - Service name doğru mu?

5. **Authentication:**
   - FeignClientInterceptor'dan gelen token doğru mu?
   - Keycloak token validation çalışıyor mu?

## İlgili Dosyalar

### Project Service:
- `src/main/java/com/cydeo/client/TaskClient.java`
- `src/main/java/com/cydeo/service/impl/ProjectServiceImpl.java` (satır 203-224)
- `src/main/java/com/cydeo/dto/wrapper/TaskResponse.java`

### Task Service (Kontrol Edilmeli):
- Controller: `/api/v1/task/count/project/{projectCode}` endpoint'i
- Service: Task count hesaplama logic'i
- DTO: Response formatı
- Exception Handler: Global exception handling

## Sonuç

**Ana Sorun:** Task service'ten 500 hatası dönüyor.  
**Çözüm:** Task service loglarını inceleyip hatayı tespit etmek ve düzeltmek gerekiyor.  
**Öncelik:** Yüksek - Bu endpoint çalışmadan project details alınamıyor.

---

**Not:** Project service tarafındaki bug düzeltildi (`return projectDTO`). Şimdi task service tarafındaki sorunu çözmek gerekiyor.


