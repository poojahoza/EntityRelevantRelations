def validate_watlinker(parser_args):
    if 'input' in parser_args and 'output' in parser_args:
        return True
    else:
        return False


def validate_stanford(parser_args):
    if 'input' in parser_args and 'output' in parser_args and 'corefflag' in parser_args and 'errorlog' in parser_args:
        return True
    else:
        return False


def validate_entityfreq(parser_args):
    if 'annotations' in parser_args and 'field' in parser_args and 'limit' in parser_args and 'output' in parser_args:
        return True
    else:
        return False


def validate_ranklib(parser_args):
    if 'qrel' in parser_args and 'feature' in parser_args and 'zscore' in parser_args and 'output' in parser_args and 'outputzscore' in parser_args and 'rankliboutput' in parser_args:
        return True
    else:
        return False
