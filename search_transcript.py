import json
import os

path = r"C:\Users\Harsh\.gemini\antigravity-ide\brain\c86a1b3f-1a5b-4c44-b0c1-8997a0616d5e\.system_generated\logs\transcript.jsonl"
print("Checking file path:", path)
if not os.path.exists(path):
    print("Path does not exist!")
    exit(1)

with open(path, "r", encoding="utf-8") as f:
    for i, line in enumerate(f):
        if "items(state.studentsForAttendance)" in line or "UpdateStudentAttendanceStatus" in line:
            try:
                data = json.loads(line)
                print(f"Line {i+1}, step_index {data.get('step_index')}:")
                # Look for tool_calls or replacementContent
                calls = data.get("tool_calls", [])
                for call in calls:
                    args = call.get("args", {})
                    if "ReplacementContent" in args:
                        print("FOUND ReplacementContent in tool call:")
                        print(args["ReplacementContent"][:1000])
                    elif "ReplacementChunks" in args:
                        print("FOUND ReplacementChunks in tool call:")
                        for chunk in args["ReplacementChunks"]:
                            print(chunk.get("ReplacementContent", "")[:1000])
                if "content" in data:
                    print("FOUND content in step:")
                    print(data["content"][:500])
            except Exception as e:
                print(f"Error parsing line {i+1}: {e}")
