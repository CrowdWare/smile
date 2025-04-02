Page {
    smlVersion: "1.1"
    title: "Buchliste"
    scrollable: "true"
    padding: "16"

    Column {
        verticalArrangement: "Top"
        horizontalAlignment: "CenterHorizontally"

        Text {
            text: "Bücherliste"
            fontSize: 24
            fontWeight: "Bold"
            padding: "0 0 16 0"
            textAlign: "Center"
        }

        LazyColumn {
            padding: "0"
            spacing: "8"

            LazyContent {
                // Beispielbuch 1
                Row {
                    horizontalAlignment: "Start"
                    verticalAlignment: "Top"
                    background: "#FFCCBC"
                    padding: "8"
                    spacing: "12"

                    Column {
                        horizontalAlignment: "Start"

                        Text {
                            text: "Buchtitel 1"
                            fontSize: 18
                            fontWeight: "Bold"
                        }
                        
                        Text {
                            text: "Autor 1"
                            fontSize: 14
                            fontWeight: "Normal"
                        }
                    }
                }

                Spacer { amount: 8 }

                // Beispielbuch 2
                Row {
                    horizontalAlignment: "Start"
                    verticalAlignment: "Top"
                    background: "#FFCCBC"
                    padding: "8"
                    spacing: "12"

                    Column {
                        horizontalAlignment: "Start"

                        Text {
                            text: "Buchtitel 2"
                            fontSize: 18
                            fontWeight: "Bold"
                        }
                        
                        Text {
                            text: "Autor 2"
                            fontSize: 14
                            fontWeight: "Normal"
                        }
                    }
                }
                
                Spacer { amount: 8 }

                // Weitere Bücher können hier hinzugefügt werden...
            }

            LazyNoContent {
                Text {
                    text: "Keine Bücher verfügbar."
                    fontSize: 16
                    fontWeight: "Normal"
                    textAlign: "Center"
                }
            }
        }
    }
}