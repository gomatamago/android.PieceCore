//
//  BaseViewController.m
//  piece
//
//  Created by ハマモト  on 2014/10/03.
//  Copyright (c) 2014年 ハマモト . All rights reserved.
//

#import "BaseViewController.h"
#import "SosialViewController.h"
#import "CoreDelegate.h"

@interface BaseViewController ()

@end

@implementation BaseViewController

//- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
//{
//    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
//    if (self) {
//        // Custom initialization
//    }
//    return self;
//}

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.viewSize = [UIScreen mainScreen].bounds.size;
    SDWebImageManager.sharedManager.delegate = self;
    self.automaticallyAdjustsScrollViewInsets = NO;
    UIBarButtonItem* btn = [[UIBarButtonItem alloc] initWithTitle:@"戻る"
                                                            style:UIBarButtonItemStylePlain
                                                           target:nil
                                                           action:nil];
    self.navigationItem.backBarButtonItem = btn;
    
    if (self.titleImgName.length > 0) {
        UIImageView *titleImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:self.titleImgName]];
        self.navigationItem.titleView = titleImageView;
    }
    
    [self viewDidLoadLogic];
}
-(void)viewDidLoadLogic{
    
}
- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [self setSosialBtn];
    [self viewDidAppearLogic];
}

-(void)viewDidAppearLogic{
    
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    [self viewWillAppearLogic];
    
}

- (void)viewWillAppearLogic
{
    
}
- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [self viewWillDisappearLogic];
}

- (void)viewWillDisappearLogic{
    
}

-(void)setSosialBtn{
    if (self.sosialSetting != nil && self.sosialBtn == nil) {
        self.sosialBtn = [UIButton buttonWithType:UIButtonTypeCustom];
        self.sosialBtn.frame = CGRectMake(self.viewSize.width * 0.8, self.viewSize.height - TabbarHight - NavigationHight, 50, 50);
        [self.sosialBtn setImage:[UIImage imageNamed:@"sns.png"] forState:UIControlStateNormal];
        [self.sosialBtn addTarget:self
                   action:@selector(moveSns:) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:self.sosialBtn];
    }
}
- (void)moveSns:(id)sender{
    SosialViewController *vc = [[SosialViewController alloc] initWithNibName:@"SosialViewController" bundle:nil];
    vc.sosialSetting = self.sosialSetting;
    [self.navigationController pushViewController:vc animated:YES];
}
-(void)receiveSucceed:(NSDictionary *)receivedData sendId:(NSString *)sendId{
    self.isResponse = YES;
    BaseRecipient *recipient = [[self getDataWithSendId:sendId] initWithResponseData:receivedData];
    if (recipient.error_code.intValue != 0) {
        [self showAlert:@"エラー" message:recipient.error_message];
     return;
     }
    if (recipient.error_message.length > 0) {
        DLog(@"%@",recipient.error_message);
    }
    [self setDataWithRecipient:recipient sendId:sendId];
    
}

-(void)receiveError:(NSError *)error sendId:(NSString *)sendId{
    CoreDelegate *delegate = [[UIApplication sharedApplication] delegate];
    if (!delegate.isUpdate) {
        NSString *errMsg;
        switch (error.code) {
            case NSURLErrorBadServerResponse:
                errMsg = @"現在メンテナンス中です。\n大変申し訳ありませんがしばらくお待ち下さい。";
                break;
            case NSURLErrorTimedOut:
                errMsg = @"通信が混み合っています。\nしばらくしてからアクセスして下さい。";
                break;
                
            default:
                break;
        }
        
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"お知らせ"
                                                        message:errMsg
                                                       delegate:self
                                              cancelButtonTitle:nil
                                              otherButtonTitles:@"OK", nil];
        [alert show];
    }
}

-(void)setDataWithRecipient:(BaseRecipient *)recipient sendId:(NSString *)sendId{
}
-(BaseRecipient *)getDataWithSendId:(NSString *)sendId{
    return nil;
}

- (void)timeoutRequest{
    [self showAlert:@"エラー" message:@"通信がタイムアウトしました。時間をおいて再度お試し下さい。"];
}
-(void)showAlert:(BaseRecipient *)recipient {
    if (![recipient.error_code isEqualToString:@"0"]) {
        [self showAlert:@"エラー" message:recipient.error_message];
    }
}

-(void)showAlert:(NSString *)title message:(NSString *)message{
    
    UIAlertView *alert =
    [[UIAlertView alloc]
     initWithTitle:title
     message:message
     delegate:nil
     cancelButtonTitle:nil
     otherButtonTitles:@"OK", nil
     ];
    
    [alert show];
    
    
    
}

@end
