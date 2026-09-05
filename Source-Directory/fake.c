#define _GNU_SOURCE
#include <dlfcn.h>
#include <sys/utsname.h>
#include <string.h>

int uname(struct utsname *buf) {
    int (*original_uname)(struct utsname *);
    original_uname = dlsym(RTLD_NEXT, "uname");
    int result = original_uname(buf);

    // 仿 Debian 样式（你可以按需再微调）
    strcpy(buf->sysname, "Linux");
    strcpy(buf->nodename, "debian-machine");  // 你主机名想要啥改这里
    strcpy(buf->release, "6.1.0-18-amd64");   // 内核版本，典型 Debian 样式
    strcpy(buf->version, "#1 SMP Debian 6.1.76-1 (2024-05-28)"); // Debian 的内核编译信息
    strcpy(buf->machine, "x86_64");           // 架构通常就是 x86_64

    return result;
}
// LD_PRELOAD=./fake.so  ./Editor/Tuanjie -logFile /dev/stdout -batchmode -serial "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImFkczo4ZDczOTk2NS02ZjMwLTQ2ZTItOGQ3MC1mZWE3MGFjNDA5ZjMifQ.eyJpc3MiOiJ1bml0eS1hZHMiLCJnZW5lc2lzSWQiOiIxMjM3MDAzMzAwNjUzMCIsInV1aWQiOiI4ZmZmODc2Mi1lYzkwLTRmMTAtMDAwMC0wYjQwMWY2YzU3YzIiLCJyb2xlcyI6W10sImlhdCI6MTc1MTcyNzYxMSwiZXhwIjoxNzUxOTg2ODExLCJzdWIiOiI2ODY5M2RmYmZmNTUzZDAyMDkxMzYxZmIiLCJqdGkiOiJmYWNiOWRmYjg0N2NmYzliZTliM2M2ZWIwM2NlMTViYmFjMzAwM2U4ZTQyNWM1Yzk3NWMzNTA4YWViZTI0MjMzIn0.UejcsulSib_F5VcgP6ArhyLlMJearfiiP1aQNmbgD-YrQepGkA-W17ThMsy9ECCpfG3Fj_f80ljKJ5s26JM4KJCHHWld4l6ZrQSdml22RJq1TZ5skhGvRrpPgqH2yQJl8XrR5Uun3qN_nrsDdMlv9SmkW46_5UNPs3BLNM4jjenJh85oaLVu01JKrCDdGX-e-XMjkU4q6VQzElY5yVAjbK2NckYNVSv8sHKwCbyLbQH8IL7KLlrFsdUrlSMqPVDgW4PuWDawavA-ulktafCjO2ZX7SeIfYPOMWWU5w99YJI_01cUwgAIGrAp6G4TCPGIOA0BiUESWfZZq6U7T0vFEQ"