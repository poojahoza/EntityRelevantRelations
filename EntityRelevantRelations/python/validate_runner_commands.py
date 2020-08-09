def validate_watlinker(parser_args):
    if not ('input' in parser_args and 'output' in parser_args):
        return False
    else:
        return True


def validate_stanford(parser_args):
    if not ('input' in parser_args and 'output' in parser_args and 'corefflag' in parser_args and 'errorlog' in parser_args):
        return False
    else:
        return True


def validate_entityfreq(parser_args):
    if not ('annotations' in parser_args and 'field' in parser_args and 'limit' in parser_args and 'output' in parser_args):
        return False
    else:
        return True


def validate_ranklib(parser_args):
    if not ('qrel' in parser_args and 'feature' in parser_args and 'zscore' in parser_args and 'output' in parser_args and 'outputzscore' in parser_args and 'rankliboutput' in parser_args):
        return False
    else:
        return True
