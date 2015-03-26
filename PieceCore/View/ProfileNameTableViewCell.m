//
//  ProfileNameTableViewCell.m
//  pieceSample
//
//  Created by ハマモト  on 2015/03/20.
//  Copyright (c) 2015年 jokerpiece. All rights reserved.
//

#import "ProfileNameTableViewCell.h"

@implementation ProfileNameTableViewCell


- (void)awakeFromNib {
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}
-(void)setInputList {
    [self initInputList];
    [self.tfList addObject:self.seiTf];
    [self.tfList addObject:self.meiTf];
}
-(void)setDataWithProfileRecipient:(ProfileRecipient *)recipient{
    if([Common isNotEmptyString:recipient.sei]){
        self.seiTf.text = recipient.sei;
    }
    if([Common isNotEmptyString:recipient.mei]){
        self.meiTf.text = recipient.mei;
    }
}
-(void)saveDataWithProfileRecipient:(ProfileRecipient *)recipient{
    recipient.sei = self.seiTf.text;
    recipient.mei = self.meiTf.text;
}
@end
