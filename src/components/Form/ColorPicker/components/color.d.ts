export enum curColorEnum {
  DEFAULT = 'default',
  USED = 'used',
  COLLECTION = 'collection',
}


export enum ColorType {
  HEX = 'hex',
  RGB = 'rgb'
}

export type curColorType = {
  type: curColorEnum | null,
  id: string | null,
  curName: ColorType | null
}