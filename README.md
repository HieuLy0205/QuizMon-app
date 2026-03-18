# QuizMon-app
QuizMon/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/quizmon/
│   │   │   │   ├── data/# Quản lý dữ liệu
│   │   │   │   │   ├── model/            # Các lớp thực thể (Question, Pet...)
│   │   │   │   │   ├── repository/       # Nơi xử lý logic lấy dữ liệu
│   │   │   │   │   └── source/           # Nguồn dữ liệu (Local/Remote)
│   │   │   │   ├── ui/                   # Giao diện người dùng
│   │   │   │   │   ├── pet/              # Màn hình/Logic liên quan đến thú cưng
│   │   │   │   │   └── quiz/             # Màn hình/Logic liên quan đến câu đố
│   │   │   │   ├── utils/                # Các lớp tiện ích (Helper classes)
│   │   │   │   └── MainActivity.kt       # Màn hình chính
│   │   │   ├── res/                      # Tài nguyên (Layout, Drawable, Strings...)
│   │   │   │   ├── layout/               # File XML giao diện
│   │   │   │   └── values/               # Màu sắc, chuỗi, kích thước...
│   │   │   └── assets/                   # Chứa file câu hỏi (questions.json)
│   └── build.gradle.kts                  # Cấu hình module app
├── build.gradle.kts                      # Cấu hình dự án (Project level)
└── settings.gradle.kts                   # Khai báo các module
<img width="854" height="562" alt="image" src="https://github.com/user-attachments/assets/bcbd36c5-ac6d-4680-a967-51f0cf53bd27" />
