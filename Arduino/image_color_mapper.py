snake_image = [
[0x000000, 0x008000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000],
[0x008000, 0x000000, 0x008000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000],
[0x008000, 0x008000, 0x008000, 0x008000, 0x008000, 0x000000, 0x000000, 0x008000],
[0x000000, 0xffffff, 0x000000, 0xffffff, 0x000000, 0x000000, 0x008000, 0x003300],
[0x000000, 0x003300, 0x008000, 0x000000, 0x000000, 0x000000, 0x008000, 0x000000],
[0x000000, 0x003300, 0x008000, 0x008000, 0x008000, 0x008000, 0x003300, 0x000000],
[0x000000, 0x003300, 0x003300, 0x003300, 0x003300, 0x003300, 0x000000, 0x000000],
[0x000000, 0x008000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000]]

overlay_image = [
[0x000000, 0x008000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000],
[0x008000, 0x000000, 0x008000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000],
[0x008000, 0x008000, 0x008000, 0x008000, 0x008000, 0x000000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000],
[0x000000, 0xffffff, 0x000000, 0xffffff, 0x000000, 0x000000, 0x008000, 0x003300, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000],
[0x000000, 0x003300, 0x008000, 0x000000, 0x000000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000],
[0x000000, 0x003300, 0x008000, 0x008000, 0x008000, 0x008000, 0x003300, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000],
[0x000000, 0x003300, 0x003300, 0x003300, 0x003300, 0x003300, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000],
[0x000000, 0x008000, 0x000000, 0x008000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000],
[0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000],
[0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000],
[0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000],
[0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000],
[0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000],
[0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000],
[0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000],
[0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000]]





colors = {"Black":  0x000000,
          "White":  0xffffff,
          "Red":    0xff0000,
          "Green":  0x00FF00,
          "Blue":   0x0000ff }

size = 16

def create_default_image(size_x=1, size_y=1, color="Black"):
    output_list = []
    for x in range(size_x):
        row = []
        for y in range(size_y):
            row.append(color)
        output_list.append(row)
    return output_list  

def print_image(input_list: list):
    output_string = ""
    for row in input_list:
        output_string += "["
        for column in row:
            output_string += (f"0x{column:06x}, ")
        output_string = output_string.rstrip(", ")
        output_string += "],\n"
    output_string = f"[{output_string[:-2]}]"
    print(output_string)

def create_defaut_string(size_x, size_y):
    output_string = ""
    for x in range(size_x):
        output_string += "{"
        for y in range(size_y):
            output_string += f"{hex(colors["Black"])}, "
        output_string += "},\n"
    output_string += "};"
    return output_string

def overlay_image(top_image: list, background: list):
    row_count = len(top_image)
    column_count = len(top_image[0])
    for row in range(row_count):
        for column in range(column_count):
            background[row][column] = top_image[row][column]
    
def main():
    output_list = create_default_image(size, size, colors["Black"])
    overlay_image(top_image=snake_image, background=output_list)
    print_image(output_list)

if __name__ == "__main__":
    main()
