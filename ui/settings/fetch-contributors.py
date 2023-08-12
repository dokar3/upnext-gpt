import requests
from textwrap import dedent, indent


def get_contributors(owner, repo):
    url = f"https://api.github.com/repos/{owner}/{repo}/contributors"
    response = requests.get(url)
    contributors = response.json()
    return contributors


def save_contributors_to_file(contributors, package, filename):
    with open(filename, 'w') as file:
        file.write(dedent(f"""\
        package {package}

        internal data class Contributor(
            val id: Long,
            val name: String,
            val avatar: String,
            val url: String,
            val contributions: Long,
        )

        """))
        file.write("internal val GH_CONTRIBUTORS = listOf(\n")
        for contributor in contributors:
            file.write(indent(
                dedent(f"""\
                    Contributor(
                        id = {contributor["id"]},
                        name = "{contributor["login"]}",
                        avatar = "{contributor["avatar_url"]}",
                        url = "{contributor["html_url"]}",
                        contributions = {contributor["contributions"]},
                    ),
                    """), 
                "    "))
        file.write(")")


contributors = get_contributors("dokar3", "upnext-gpt")

save_contributors_to_file(
    contributors,
    "io.upnextgpt.ui.settings",
    "src/main/java/io/upnextgpt/ui/settings/Contributors.kt"
)
