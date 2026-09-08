#!/opt/homebrew/bin/python3
import os

INDEX_NAMES = {"index.html", "index.htm", "index.php"}
TARGET_DIR = "public_html"


def generate_index_content(rel_path, items):
    links = []
    
    # Format the header path to match default directory listing format
    if rel_path == ".":
        display_path = "/"
    else:
        display_path = f"/{rel_path}/"

    # Add parent directory link if we are in a subdirectory
    if rel_path != ".":
        links.append('<li><a href="../">../</a></li>')

    # Sort directories first, then files (case-insensitive)
    dirs = sorted([i for i in items if i["is_dir"]], key=lambda x: x["name"].lower())
    files = sorted([i for i in items if not i["is_dir"]], key=lambda x: x["name"].lower())

    for d in dirs:
        links.append(f'<li><a href="{d["name"]}/">{d["name"]}/</a></li>')
    for f in files:
        links.append(f'<li><a href="{f["name"]}">{f["name"]}</a></li>')

    links_html = "\n".join(links)

    return f"""<!DOCTYPE html>
<html lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style type="text/css">
:root {{
color-scheme: light dark;
}}
</style>
<title>Directory listing for {display_path}</title>
</head>
<body>
<h1>Directory listing for {display_path}</h1>
<hr>
<ul>
{links_html}
</ul>
<hr>
</body>
</html>"""


def process_directories(root_dir):
    if not os.path.exists(root_dir):
        print(f"Error: Directory '{root_dir}' does not exist.")
        return

    for current_path, dirs, files in os.walk(root_dir):
        # Ignore hidden files/folders (starting with .)
        files = [f for f in files if not f.startswith(".")]
        dirs[:] = [d for d in dirs if not d.startswith(".")]

        # Check if any index file already exists in this folder
        existing_indexes = set(files).intersection(INDEX_NAMES)
        if existing_indexes:
            print(f"[SKIP] {current_path} (already has {', '.join(existing_indexes)})")
            continue

        # Prepare list of items to show in the generated index
        items = []
        for d in dirs:
            items.append({"name": d, "is_dir": True})
        for f in files:
            items.append({"name": f, "is_dir": False})

        # Generate and write index.html
        rel_path = os.path.relpath(current_path, root_dir)
        html = generate_index_content(rel_path, items)
        index_path = os.path.join(current_path, "index.html")

        with open(index_path, "w", encoding="utf-8") as f:
            f.write(html)

        print(f"[CREATED] {index_path}")


if __name__ == "__main__":
    process_directories(TARGET_DIR)