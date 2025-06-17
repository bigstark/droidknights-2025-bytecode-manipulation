# Bytecode Manipulation - click count
## 발표 자료
발표 자료 : [링크](https://speakerdeck.com/bigstark/bytecode-manipulation-euro-saengsanseong-nopigi)

### 사전 준비
1. composable click log plugin 을 빌드하여 maven local 에 publish 

```
./gradlew :composable-click-log-plugin:publishToMavenLocal
```

2. `./gradlew :app:assembleDebug` 를 통해 빌드 (혹은 안드로이드 스튜디오에서 run)

ClickCountActivity 내에서 `setOnClickListener` 코드가 존재하면 `onClick` 최상단에 아래의 코드를 삽입합니다.
### AS-IS
```kotlin
// 예를들어 아래의 코드가 있다고 가정한다면
binding.button.setOnClickListener {
    Toast.makeText(this, "Hello World droid knights!", Toast.LENGTH_SHORT).show()
}
```
### Transformed
```kotlin
binding.button.setOnClickListener {
    // 아래의 코드를 삽입
    val count = it.tag as? Int ?: 0
    it.tag = count + 1
    Log.v("TAG", "count: ${it.tag}")
    
    // 기존 코드 시작
    Toast.makeText(this, "Hello World droid knights!", Toast.LENGTH_SHORT).show()
}
```

# Bytecode Manipulation - click log
ClickLogActivity 내에서 `setOnClickListener` 익명함수에 `@Loggable` 이 있다면, `onClick` 최상단에 로깅 코드를 삽입합니다.
### AS-IS
```kotlin
// 예를들어 아래의 코드가 있다고 가정한다면
binding.button.setOnClickListener @Loggable("clicked_btn_show_toast") {
    Log.v("TAG", "button clicked")
}
```

### Transformed
```kotlin
// transform
binding.button.setOnClickListener {
    // 기존 코드 시작
    Log.v("TAG", "button clicked")
    
    // 아래의 코드를 삽입
    Log.v("TAG", "clicked_btn_show_toast")
}
```

# Kotlin Compiler Plugin - composable click log
ComposableClickLogActivity 내에서 `clickable` 의 익명함수에 로깅 코드를 삽입합니다.
### AS-IS
```kotlin
@Composable
fun ExampleScreen() {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .background(
                        color = Color.Green,
                        shape = CircleShape
                    )
                    .clickable {
                        toast()
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Click Me!")
            }
        }
    }
}

private fun toast() {
    Toast.makeText(this, "Hello World droid knights!", Toast.LENGTH_SHORT).show()
}
```

### Transformed
```kotlin
@Composable
fun ExampleScreen() {
    MaterialTheme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .background(
                        color = Color.Green,
                        shape = CircleShape
                    )
                    .clickable {
                        // 아래의 코드를 삽입
                        Log.v("TAG", "Hello World droid knights 2025!")
                        
                        // 기존코드 시작
                        toast()
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Click Me!")
            }
        }
    }
}

private fun toast() {
    Toast.makeText(this, "Hello World droid knights!", Toast.LENGTH_SHORT).show()
}
```
