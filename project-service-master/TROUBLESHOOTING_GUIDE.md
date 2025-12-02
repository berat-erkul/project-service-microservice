# Spring Boot Microservice Sorun Giderme Rehberi

## Karşılaşılan Sorunlar ve Çözümleri

### 1. Lombok - Java 21 Uyumsuzluğu

**Sorun:**
```
java.lang.IllegalAccessError: class lombok.javac.apt.LombokProcessor 
cannot access class com.sun.tools.javac.processing.JavacProcessingEnvironment
```

**Çözüm:**
`pom.xml`'de Lombok için açık versiyon belirt:
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
</dependency>
```

---

### 2. Feign Client LoadBalancing Hatası

**Sorun:**
```
No Feign Client for loadBalancing defined. 
Did you forget to include spring-cloud-starter-netflix-ribbon?
```

**Çözüm Adımları:**

1. **pom.xml'e LoadBalancer dependency ekle:**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-loadbalancer</artifactId>
</dependency>
```

2. **Ribbon exclusion'larını ekle (Eureka Client dependency'sinde):**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
        </exclusion>
        <exclusion>
            <groupId>com.netflix.ribbon</groupId>
            <artifactId>ribbon-eureka</artifactId>
        </exclusion>
        <exclusion>
            <groupId>javax.ws.rs</groupId>
            <artifactId>jsr311-api</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

3. **application.yml'e LoadBalancer yapılandırması ekle:**
```yaml
spring:
  cloud:
    loadbalancer:
      ribbon:
        enabled: false
```

---

### 3. Java 21 - Eureka/XStream Uyumsuzluğu

**Sorun:**
```
java.lang.reflect.InaccessibleObjectException: 
Unable to make field private final java.util.Comparator 
java.util.TreeMap.comparator accessible: 
module java.base does not "opens java.util" to unnamed module
```

**Çözüm:**
`pom.xml`'de Spring Boot Maven plugin'ine JVM argümanları ekle:
```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <jvmArguments>
            --add-opens java.base/java.util=ALL-UNNAMED
            --add-opens java.base/java.lang.reflect=ALL-UNNAMED
            --add-opens java.base/java.text=ALL-UNNAMED
            --add-opens java.desktop/java.awt.font=ALL-UNNAMED
        </jvmArguments>
    </configuration>
</plugin>
```

---

## Hızlı Kontrol Listesi

### Yeni bir serviste sorun giderme adımları:

1. ✅ **Java versiyonunu kontrol et:**
   ```bash
   java -version
   mvn -version
   ```

2. ✅ **Lombok versiyonu var mı kontrol et:**
   - Yoksa `1.18.30` ekle

3. ✅ **Feign Client kullanılıyor mu?**
   - LoadBalancer dependency var mı?
   - `application.yml`'de `spring.cloud.loadbalancer.ribbon.enabled: false` var mı?
   - Ribbon exclusion'ları var mı?

4. ✅ **Java 21 kullanılıyor mu?**
   - JVM argümanları eklendi mi?

5. ✅ **Derleme yap:**
   ```bash
   mvn clean compile
   ```

6. ✅ **Uygulamayı çalıştır:**
   ```bash
   mvn spring-boot:run
   ```

---

## Önemli Notlar

- **Spring Cloud Hoxton.SR8** ile **Java 21** kullanırken JVM argümanları şarttır
- **LoadBalancer** kullanırken Ribbon'u **mutlaka exclude** et
- **user-service** ile **project-service** aynı yapılandırmaya sahip olmalı
- Port temizleme: `lsof -ti :PORT | xargs kill -9`

---

## Başarı Kriterleri

✅ Derleme başarılı (`mvn clean compile`)  
✅ Uygulama başlatılıyor (`mvn spring-boot:run`)  
✅ Port dinleniyor (`lsof -i :PORT`)  
✅ Eureka'ya kayıt oluyor (loglarda görünür)  
✅ Feign Client hatası yok


