import genanki
import json
import sys
import random
import os

MAX_SQLITE_INT = 2**63 - 1

# ✅ Caminho absoluto direto para os templates
RESOURCE_DIR = "/home/zeus/IdeaProjects/parser-kt/src/main/kotlin/com/zeuskorps/parserkt/infrastructure/python_apkg_adapter/infrastructure/resources"

with open(os.path.join(RESOURCE_DIR, "front_template.html"), "r", encoding="utf-8") as f:
    front_template = f.read()

with open(os.path.join(RESOURCE_DIR, "back_template.html"), "r", encoding="utf-8") as f:
    back_template = f.read()

with open(os.path.join(RESOURCE_DIR, "style.css"), "r", encoding="utf-8") as f:
    style_template = f.read()

def generate_safe_id():
    return random.randint(1, MAX_SQLITE_INT)

def build_model():
    return genanki.Model(
        1234567890,  # ID fixo para evitar duplicações no Anki
        'parserkt',
        fields=[
            {'name': 'Front'},
            {'name': 'Back'},
        ],
        templates=[
            {
                'name': 'Card 1',
                'qfmt': front_template,
                'afmt': back_template,
            },
        ],
        css=style_template,
    )

def section(label, content):
    return f"### {label}\n\n{content.strip()}" if content.strip() else ""

def format_front(card):
    universe = section("Universo", card.get("universe", ""))
    question = section("Pergunta", card.get("question", ""))
    return f"{universe}\n\n{question}".strip()

def format_back(card):
    parts = [
        section("Resposta", card.get("response", "")),
        section("Exemplo", card.get("example", "")),
        section("Contraexemplo", card.get("counterExample", "")),
        section("Correção do Contraexemplo", card.get("counterExampleCorrection", "")),
        section("Desafio", card.get("challenge", "")),
    ]
    return "\n\n".join([part for part in parts if part]).strip()

def build_deck(deck_name, flashcards, model):
    deck = genanki.Deck(generate_safe_id(), deck_name)
    for card in flashcards:
        front = format_front(card)
        back = format_back(card)

        if not front.strip() or not back.strip():
            continue

        note = genanki.Note(
            model=model,
            fields=[front, back],
            guid=str(generate_safe_id())
        )
        deck.add_note(note)
    return deck

def load_flashcards(path):
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)

def main():
    if len(sys.argv) != 4:
        print("❌ Uso esperado: python generate_apkg.py <json_path> <output_path> <deck_name>")
        sys.exit(1)

    json_path, output_path, deck_name = sys.argv[1], sys.argv[2], sys.argv[3]

    if not os.path.exists(json_path):
        print(f"❌ Arquivo JSON não encontrado: {json_path}")
        sys.exit(1)

    print(f"📖 Carregando flashcards de: {json_path}")
    flashcards = load_flashcards(json_path)

    print(f"🛠️  Construindo modelo e deck: {deck_name}")
    model = build_model()
    deck = build_deck(deck_name, flashcards, model)

    print(f"💾 Salvando pacote em: {output_path}")
    genanki.Package(deck).write_to_file(output_path)
    print("✅ Pacote .apkg gerado com sucesso!")

if __name__ == "__main__":
    main()
