# 😄 smile CLI

**smile** and also **SML** stands for **Simple Markup Language** – a declarative language to describe user interfaces.  
This CLI tool is the core of the SML ecosystem. It helps you scaffold UI projects, compile SML into real Compose code, build native apps, and export through powerful plugins.

> "Don't code it. Don't build it. Just... smile it."

---

## ✨ Features

- 🏗️ `smile new <project>` — Scaffolds a ready-to-go SML UI project
- 🛠️ `smile build` — Build for Desktop, Android or WASM
- 🧠 `smile compose` — Converts `.sml` files into Compose UI code
- 🔌 `smile export` — Plugin system for outputs like `ebook3`, `bootstrap`, `qtcpp`
- 🎨 Supports `TransitionDefinition`, `Theme`, and `PluginDefinition` in SML
- 🤖 AI-ready – easy to generate via natural language (Prompt2SML coming)

---

## 🚀 Quickstart

```bash
git clone https://github.com/CrowdWare/smile.git
cd smile

# Build smile
./gradlew build
./gradlew nativeImage

# Create a new project
./smile new yoga-app

# Navigate to project
cd yoga-app

# Build for desktop
../smile build --target=desktop

# Or run via Gradle
./gradlew run --args="new yoga-app"
```

---

## 📁 Project Structure

A typical smile project looks like this:

```bash
myapp/
├── app.sml
├── pages/
│   └── home.sml
```

## 🧑‍💻 Example: app.sml

```bash
App {
    name: "yoga-app"
    startPage: "home"

    Theme {
        primary: "#FF5722"
        onPrimary: "#FFFFFF"
    }
}
```

## 💡 Example: home.sml
```bash
Page {
    Column {
        Markdown { text: "# Welcome 👋" }
        Button { label: "Get Started" link: "page:next" }
    }
}
````

---

## 🧰 CLI Commands

| Command | Description |
|---|---|
|smile new <project> | Scaffold a new SML project |
|smile build --target=...|Build the project (desktop, wasm, android)|
|smile compose <src>|Generate Compose code from .sml files|
|smile export --plugin=...|Export via plugin (e.g. ebook3, qtcpp)|
|smile preview|Start live preview (optional)|
|smile version|Show CLI version|

---

## 🔌 Plugin System

The smile CLI shares the same plugin engine as the NoCodeDesigner.
Plugins are defined via .jar and plugin.json and can be reused in both tools.

✅ The core is open source and forever free.
💰 Some advanced plugins like ebook3-plugin, qtcpp-plugin, bootstrap-plugin are commercial.

---

## 📦 Real-World Use
• The NoCodeDesigner uses smile and sml.it to power real UI generation.
• SML is designed for both developer tooling and AI-assisted design.
• Future integration with Prompt2UI engines (like ChatGPT, Claude, etc.) is planned.

## 📄 License  
• smile CLI – GPL-3, open source and forever free
• Plugins – Some are commercial (license required)  

---
## Plugin to Wordpress
we can now write a Wordpress plugin to include the HTML output from the docs folder into a page in Wordpress.

```php
<?php
/**
 * Plugin Name: SML Renderer
 * Description: Lädt ein gerendertes SML-HTML von GitHub Pages und zeigt den <body>-Inhalt.
 * Version: 1.0
 * Author: Crowdware
 */

function render_sml_shortcode($atts) {
    $a = shortcode_atts([
        'src' => '',
    ], $atts);

    if (empty($a['src'])) return '<!-- SML: src fehlt -->';

    $url = 'https://crowdware.github.io/smile/' . ucfirst($a['src']) . '.html';

    $html = @file_get_contents($url);
    if (!$html) return "<!-- SML konnte nicht geladen werden: {$url} -->";

    // <body> extrahieren
    if (preg_match('/<body[^>]*>(.*?)<\/body>/is', $html, $matches)) {
        return $matches[1];
    } else {
        return "<!-- Kein <body> im Ergebnis -->";
    }
}

add_shortcode('sml-render', 'render_sml_shortcode');
```

---

## 🌐 Learn More
• 🌍 Homepage: https://sml.it
• 📚 Docs: docs.sml.it (coming soon)
• 🧠 Prompt2UI Playground: chat.sml.it (planned)

---

Made with ❤️ by [Crowdware](https://crowdware.info)