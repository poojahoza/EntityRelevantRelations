import json
import os
import math

from json.decoder import JSONDecodeError


def read_multiple_json_files(folder_location):
    files = os.listdir(folder_location)
    content_json = []
    try:
        for file in files:
            with open(folder_location+'/'+file, 'r', encoding='utf-8') as f:
                content_json.extend(json.load(f))
        return content_json
    except Exception as e:
        print(e)
        return None


def write_multiple_json_files(folder_location, output_json, output_file_name):

    file_limit = 50000
    num_files = math.ceil(len(output_json)/file_limit)
    counter = -1
    temp_json_list = []
    for i in xrange(1, num_files+1):
        for y in xrange((counter + 1), (file_limit * i)):
            counter = counter + 1
            if len(temp_json_list) >= y:
                try:
                    temp_json_list.append(output_json[y])
                except IndexError:
                    print(y)
        if len(temp_json_list) > 0:
            output_file = folder_location+'/'+output_file_name+'_'+i+'.json'
            write_json_file(output_file, temp_json_list)


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


def read_converted_entity_ids_reversed(folder_location):
    files = os.listdir(folder_location)
    converted_entity_ids = dict()
    try:
        for file in files:
            with open(folder_location+'/'+file, 'r', encoding='utf-8') as f:
                for line in f:
                    data = line.split('\t')

                    # the titles contain \n at the end, hack to resolve that
                    title = data[1].split('\n')[0]
                    for i in range(1,len(data)):
                        converted_entity_ids[data[i]] = title

        return converted_entity_ids
    except Exception as e:
        print(e)
        return None


def write_text_file(file_location, output_list):
    with open(file_location, 'w', encoding='utf-8') as f:
        for line in output_list:
            f.write('%s\n' % line)
