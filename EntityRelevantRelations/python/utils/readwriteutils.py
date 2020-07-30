import json
import os

from json.decoder import JSONDecodeError


def read_json_file(file_location):
    try:
        with open(file_location, 'r', encoding='utf-8') as f:
            json_dict = json.load(f)
        return json_dict
    except JSONDecodeError as e:
        print(e)
        return None


def write_json_file(file_location, output_json):
    try:
        with open(file_location, 'w', encoding='utf-8') as f:
            json.dump(output_json, f)
            return True
    except TypeError as t:
        print(t)
        return False


def read_converted_entity_ids(folder_location):
    files = os.listdir(folder_location)
    converted_entiy_ids = dict()
    try:
        for file in files:
            with open(folder_location+file, 'r', encoding='utf-8') as f:
                for line in f:
                    data = line.split('\t')
                    entity_ids_list = []
                    if data[1] in converted_entiy_ids:
                        entity_ids_list = converted_entiy_ids[data[1]]

                    entity_ids_list.append(data[0])
                    converted_entiy_ids[data[1]] = entity_ids_list
        return converted_entiy_ids
    except Exception as e:
        print(e)
        return None


def write_text_file(file_location, output_list):
    with open(file_location, 'w', encoding='utf-8') as f:
        for line in output_list:
            f.write('%s\n' % line)
