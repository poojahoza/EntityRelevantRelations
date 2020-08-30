import json
import argparse
import os


def read_converted_entity_ids(folder_location):
    files = os.listdir(folder_location)
    converted_entiy_ids = dict()
    try:
        for file in files:
            with open(folder_location+'/'+file, 'r', encoding='utf-8') as f:
                for line in f:
                    data = line.split('\t')
                    entity_ids_list = []

                    # the titles contain \n at the end, hack to resolve that
                    title = data[1].split('\n')[0]
                    if title in converted_entiy_ids:
                        entity_ids_list = converted_entiy_ids[title]

                    entity_ids_list.append(data[0])
                    converted_entiy_ids[title] = entity_ids_list
        return converted_entiy_ids
    except Exception as e:
        print(e)
        return None


def convert_annotations_to_wat_annotations_schema(input_file_loc, entity_id_conv_folder, output_file_loc):

    final_output_json = []

    converted_entity_ids = read_converted_entity_ids(entity_id_conv_folder)

    with open(input_file_loc, 'r') as f:
        input_json = json.load(f)

    for item in input_json:
        item_dict = dict()
        item_dict['queryid'] = item['queryid']
        item_dict['contextid'] = item['contextid']
        item_dict['contexttext'] = item['contexttext']
        item_dict['contextrank'] = item['contextrank']
        item_dict['contextscore'] = item['contextscore']
        wat_annotations = []
        for ann in item['WATannotations']:
            wat_annotations.append(converted_entity_ids[ann['wiki_title']])

        item_dict['WATEntitiesTitle'] = wat_annotations
        final_output_json.append(item_dict)

    with open(output_file_loc, 'w', encoding='utf-8') as f1:
        json.dump(final_output_json, f1)


if __name__ == "__main__":
    parser = argparse.ArgumentParser("Please provide input annotations file, entity id conversions folder and "
                                     "output file path")
    parser.add_argument("--i", help="Input relation annotation JSON file location")
    parser.add_argument("--e", help="Input entity id conversions folder location")
    parser.add_argument("--o", help="Output JSON file location")
    args = parser.parse_args()
    convert_annotations_to_wat_annotations_schema(args.i, args.e, args.o)
