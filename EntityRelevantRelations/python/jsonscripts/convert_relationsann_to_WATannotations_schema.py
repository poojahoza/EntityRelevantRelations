import json
import argparse


def convert_relation_annotations_to_wat_annotations_schema(input_file, output_file):

    final_output_json = []
    with open(input_file, 'r') as f:
        input_json = json.load(f)
    for item in input_json:
        item_dict = dict()
        item_dict['queryid'] = item['queryid']
        item_dict['contextid'] = item['contextid']
        item_dict['contexttext'] = item['contexttext']
        item_dict['contextrank'] = item['contextrank']
        item_dict['contextscore'] = item['contextscore']
        rel_annotations = []
        for rel in item['relAnnotations']:
            for sub in rel['subjectAnnotations']:
                for entityid in sub['wiki_converted_id']:
                    rel_annotations.append(entityid)
            for obj in rel['objectAnnotations']:
                for ent in obj['wiki_converted_id']:
                    rel_annotations.append(ent)

        item_dict['WATEntitiesTitle'] = rel_annotations
        final_output_json.append(item_dict)

    with open(output_file, 'w', encoding='utf-8') as f1:
        json.dump(final_output_json, f1)


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide input para aggr jsonl file and output file path")
    parser.add_argument("--i", help="Input relation annotation JSON file location")
    parser.add_argument("--o", help="Output JSON file location")
    args = parser.parse_args()
    convert_relation_annotations_to_wat_annotations_schema(args.i, args.o)
