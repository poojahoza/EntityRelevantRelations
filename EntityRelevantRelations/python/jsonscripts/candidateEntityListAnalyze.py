import os

def process_json_files(input_json_dir):
    files = os.listdir(input_json_dir)
    for file in files:
        print(file)

if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide input JSON directory, Qrel file and output file path")
    parser.add_argument("--i",help="Input JSON folder location")
    parser.add_argument("--q",help="Input qrel file location")
    parser.add_argument("--o",help="Output JSON file location")
    args = parser.parse_args()
    process_json_files(args.i)