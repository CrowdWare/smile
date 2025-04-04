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

    $url = 'https://crowdware.github.io/smile/' . $a['src'] . '.html';

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